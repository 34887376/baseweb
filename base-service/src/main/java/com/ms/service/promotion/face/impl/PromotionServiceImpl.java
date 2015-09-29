package com.ms.service.promotion.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.domain.convert.PromotionConvert;
import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.bo.PromotionInfoBO;
import com.ms.domain.promotion.dao.PromotionDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.promotion.face.IPromotionService;
import com.ms.dao.promotion.face.IPromotionDAO;
import com.ms.dao.promotion.redis.IPromotionRedis;
import com.ms.dao.promotionsequence.face.IPromotionSequenceDAO;
import com.ms.dao.sku.face.ISkuDAO;
import com.ms.dao.ladder.face.ILadderDAO;
import com.ms.dao.ladderpromotion.face.ILadderPromotion;

public class PromotionServiceImpl implements IPromotionService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	//促销DAO管理接口
	private IPromotionDAO iPromotionDAO;
	
	//阶梯规则DAO管理接口
	private ILadderDAO iLadderDAO;
	
	//商品DAO管理接口
	private ISkuDAO iSkuDAO;
	
	//促销序列DAO管理接口
	private IPromotionSequenceDAO iPromotionSequenceDAO;
	
	//阶梯促销关系DAO管理接口
	private ILadderPromotion iLadderPromotion;
	
	//操作redis的方法
	private IPromotionRedis iPromotionRedis;
	

	public PromotionBO queryPromotionById(Long id) {
		PromotionBO promotionBO =new PromotionBO();
		try {
			PromotionDAO promotionDAO = iPromotionDAO.queryPromotionById(id);
			promotionBO = PromotionConvert.convertDAOToBO(promotionDAO);
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.queryPromotionById查询促销信息时发生异常，入参{id="+id+"}", e);
		}
		return promotionBO;
	}

	public List<PromotionBO> queryPromotionsByIds(List<Long> idList) {
		List<PromotionBO> promotionList = new ArrayList<PromotionBO>();
		try {
			List<PromotionDAO> promotionDAOList = iPromotionDAO.queryPromotionsByIds(idList);
			if(CollectionUtils.isNotEmpty(promotionDAOList)){
				for(PromotionDAO promotionDAO : promotionDAOList){
					promotionList.add(PromotionConvert.convertDAOToBO(promotionDAO));
				}
			}
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.queryPromotionsByIds查询促销信息时发生异常，入参{idList="+JsonUtil.toJson(idList)+"}", e);
		}
		return promotionList;
	}

	public long addPromotion(PromotionBO promotionBO) {
		if(promotionBO==null){
			return -1;
		}
		try {
			return iPromotionDAO.addPromotion(PromotionConvert.convertBOToDAO(promotionBO));
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.addPromotion添加信息时发生异常，入参{promotionBO="+JsonUtil.toJson(promotionBO)+"}", e);
		}
		return -1;
	}

	public boolean delPromotionsByIds(List<Long> idList) {
		try {
			return iPromotionDAO.delPromotionsByIds(idList);
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.delPromotionsByIds删除促销信息时发生异常，入参{idList="+JsonUtil.toJson(idList)+"}", e);
		}
		return false;
	}

	public boolean updatePromotion(PromotionBO promotionBO) {
		try {
			return iPromotionDAO.updatePromotion(PromotionConvert.convertBOToDAO(promotionBO));
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.updatePromotion更新促销信息时发生异常，入参{promotionBO="+JsonUtil.toJson(promotionBO)+"}", e);
		}
		return false;
	}

	public List<PromotionInfoBO> queryLastNumPromotion(int num) {
		List<PromotionInfoBO> promotionList = new ArrayList<PromotionInfoBO>();
		try {
			List<Long> promotionIdList = new ArrayList<Long>();
			String startPromotionIndexKey=RedisKeyPrefixConstant.START_PROMOTION_INDEX;
			String startPromotionId = iPromotionRedis.getValue(startPromotionIndexKey);
			if(StringUtils.isBlank(startPromotionId)){
				return promotionList;
			}
			promotionIdList.add(Long.parseLong(startPromotionId));
			
			//查询redis中的前num个促销信息
			for(int promotionNum=0;promotionNum<num;promotionNum++){
				String nextPromotionId = iPromotionRedis.getValue(startPromotionIndexKey+startPromotionId);
				if(StringUtils.isNotBlank(nextPromotionId)){
					promotionIdList.add(Long.parseLong(nextPromotionId));
				}
			}
			
			if(CollectionUtils.isEmpty(promotionIdList)){
				return promotionList;
			}
			
			//如果查询出的促销信息不够num个，则再进行10次redis查询，如果查询10次redis仍然不够num个，则查到多少返回多少
			int hasPromotionNum = promotionIdList.size();
			if(hasPromotionNum<num){
				long lastPromotionId = promotionIdList.get((hasPromotionNum-1));
				for(int j=0;j<10;j++){
					String nextPromotionId = iPromotionRedis.getValue(startPromotionIndexKey+lastPromotionId);
					if(StringUtils.isNotBlank(nextPromotionId)){
						promotionIdList.add(Long.parseLong(nextPromotionId));
						if(promotionIdList.size()>=num){
							break;
						}
					}
				}
			}
			
			List<String> promotionKeyList = new ArrayList<String>();
			for(Long promotionId : promotionIdList){
				promotionKeyList.add(RedisKeyPrefixConstant.PROMOTION_INDEX+promotionId);
			}
			//从redis中查询促销信息，如果查询不到，则查询数据库，并写入到redis中
			List<String> promotionInfos = iPromotionRedis.getValuesByKeyList(promotionKeyList);
			if(CollectionUtils.isNotEmpty(promotionInfos)){
				for(String promotionStr : promotionInfos){
					PromotionInfoBO promotionInfoBO = JsonUtil.fromJson(promotionStr, PromotionInfoBO.class);
					promotionList.add(promotionInfoBO);
				}
			}else{
				promotionList = this.queryPromotionsByIds(promotionIdList);
			}
		
			
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.updatePromotion更新促销信息时发生异常，入参{promotionBO="+JsonUtil.toJson(num)+"}", e);
		}
		return promotionList;
	}

}
