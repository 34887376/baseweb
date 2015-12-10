package com.ms.service.promotion.face.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.domain.convert.PromotionConvert;
import com.ms.domain.ladder.dao.LadderDAO;
import com.ms.domain.ladderpromotion.bo.LadderPromotionInfoBO;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;
import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.bo.PromotionInfoBO;
import com.ms.domain.promotion.dao.PromotionDAO;
import com.ms.domain.promotionsequence.dao.PromotionSequenceDAO;
import com.ms.domain.sku.dao.SkuDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.promotion.face.IPromotionService;
import com.ms.dao.promotion.face.IPromotionDAO;
import com.ms.dao.promotion.redis.IPromotionRedis;
import com.ms.dao.promotionsequence.face.IPromotionSequenceDAO;
import com.ms.dao.sku.face.ISkuDAO;
import com.ms.dao.ladder.face.ILadderDAO;
import com.ms.dao.ladderpromotion.face.ILadderPromotionDAO;

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
	private ILadderPromotionDAO iLadderPromotion;
	
	//操作redis的方法
	private IPromotionRedis iPromotionRedis;
	

	public PromotionBO queryPromotionById(Long id) {
		PromotionBO promotionBO =new PromotionBO();
		try {
			PromotionDAO promotionDAO = iPromotionDAO.queryPromotionById(id);
			promotionBO = PromotionConvert.convertDAOTOBO(promotionDAO);
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
					promotionList.add(PromotionConvert.convertDAOTOBO(promotionDAO));
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
			return iPromotionDAO.addPromotion(PromotionConvert.convertBOTODAO(promotionBO));
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
			return iPromotionDAO.updatePromotion(PromotionConvert.convertBOTODAO(promotionBO));
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.updatePromotion更新促销信息时发生异常，入参{promotionBO="+JsonUtil.toJson(promotionBO)+"}", e);
		}
		return false;
	}

	public List<PromotionInfoBO> queryLastNumPromotion(int num) {
		List<PromotionInfoBO> promotionInfoBOList = new ArrayList<PromotionInfoBO>();
		try {
			List<Long> promotionIdList = new ArrayList<Long>();
			//获取设置的第一条促销id
			String startPromotionIndexKey=RedisKeyPrefixConstant.START_PROMOTION_INDEX;
			String startPromotionId = iPromotionRedis.getValue(startPromotionIndexKey);
			if(StringUtils.isBlank(startPromotionId)){
				return promotionInfoBOList;
			}
			promotionIdList.add(Long.parseLong(startPromotionId));
			
			//查询redis中的前num个促销信息;如果缓存中没有则查库，并写入到缓存
			for(int promotionNum=0;promotionNum<num;promotionNum++){
				//获取该促销id之后的下一个促销id；这个是在后台设置的
				String nextPromotionId = iPromotionRedis.getValue(startPromotionIndexKey+startPromotionId);
				if(StringUtils.isNotBlank(nextPromotionId)){
					promotionIdList.add(Long.parseLong(nextPromotionId));
					startPromotionId = nextPromotionId;
				}else{
					PromotionSequenceDAO promotionSequenceDAO = new PromotionSequenceDAO();
					promotionSequenceDAO.setPromotionId(Long.parseLong(startPromotionId));
					//根据startPromotionId查询该促销信息
					List<PromotionSequenceDAO> promotionSequence = iPromotionSequenceDAO.queryPromotionSequenceByCondition(promotionSequenceDAO);
					if(CollectionUtils.isNotEmpty(promotionSequence)){
						PromotionSequenceDAO promotionSequenceFromDB = promotionSequence.get(0);
						PromotionSequenceDAO nextPromoParam = new PromotionSequenceDAO();
						//根据该促销信息查询再该促销信息之后的下一个促销信息
						nextPromoParam.setId(promotionSequenceFromDB.getNextOrder());
						List<PromotionSequenceDAO> nextPromotion = iPromotionSequenceDAO.queryPromotionSequenceByCondition(nextPromoParam);
						if(CollectionUtils.isNotEmpty(nextPromotion)){
							//设置该促销信息的下一个促销信息到redis
							iPromotionRedis.setValue(startPromotionIndexKey+startPromotionId, nextPromotion.get(0).getPromotionId().toString());
							promotionIdList.add(nextPromotion.get(0).getPromotionId());
							startPromotionId = nextPromotion.get(0).getPromotionId().toString();
						}else{
							//如果没有查到促销信息则认为数据库中没有该信息，则设置其为null,有效期为5*60秒
							iPromotionRedis.setValue(startPromotionIndexKey+startPromotionId, null,300);
						}
					}
				}
			}
			
			if(CollectionUtils.isEmpty(promotionIdList)){
				return promotionInfoBOList;
			}
			//从redis中查询促销信息，如果查询不到，则查询数据库，并写入到redis中
			promotionInfoBOList = getPromotionInfoFromRedis(promotionIdList,num);
			if(CollectionUtils.isNotEmpty(promotionInfoBOList)){
				return promotionInfoBOList;
			}else{
				return getPromotionInfoFromDB(promotionIdList);
			}
		
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.queryLastNumPromotion查询促销信息时发生异常，入参{num="+JsonUtil.toJson(num)+"}", e);
		}
		return promotionInfoBOList;
	}
	
	/**
	 * 从redis中查询促销信息
	 * @param promotionIdList
	 * @param num
	 * @return
	 */
	private List<PromotionInfoBO> getPromotionInfoFromRedis(List<Long> promotionIdList, int num){
		List<PromotionInfoBO> promotionInfoBOList = new ArrayList<PromotionInfoBO>();
		try {
			
			String startPromotionIndexKey=RedisKeyPrefixConstant.START_PROMOTION_INDEX;
			
			//如果查询出的促销信息不够num个，则再进行10次redis查询，如果查询10次redis仍然不够num个，则查到多少返回多少
			int hasPromotionNum = promotionIdList.size();
			if(hasPromotionNum<num){
				String lastPromotionId = String.valueOf(promotionIdList.get((hasPromotionNum-1)));
				for(int j=0;j<10;j++){
					String nextPromotionId = iPromotionRedis.getValue(startPromotionIndexKey+lastPromotionId);
					if(StringUtils.isNotBlank(nextPromotionId)){
						promotionIdList.add(Long.parseLong(nextPromotionId));
						if(promotionIdList.size()>=num){
							break;
						}
					}
					lastPromotionId=nextPromotionId;
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
					promotionInfoBOList.add(promotionInfoBO);
				}
			}
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.updatePromotion更新促销信息时发生异常，入参{promotionBO="+JsonUtil.toJson(num)+"}", e);
		}
		return promotionInfoBOList;
	}
	
	/**
	 * 从数据库中查询促销信息，并将热点促销信息刷入到缓存中
	 * @param promotionIdList
	 * @return
	 */
	private List<PromotionInfoBO> getPromotionInfoFromDB(List<Long> promotionIdList){
		
		List<PromotionInfoBO> promotionInfoBOList = new ArrayList<PromotionInfoBO>();
		try{
			
			//1. 查询促销列表
			List<PromotionBO> promotionBOList = this.queryPromotionsByIds(promotionIdList);
			for(PromotionBO promotionBO : promotionBOList){
				PromotionInfoBO promotionInfoBO = new PromotionInfoBO();
				promotionInfoBO.setSkuId(promotionBO.getSkuId());
				
				//2. 查询商品信息
				SkuDAO skuInfo = iSkuDAO.querySkuById(promotionBO.getSkuId());
				promotionInfoBO.setAdvert(skuInfo.getAdverst());
				promotionInfoBO.setSkuImgUrl(skuInfo.getImgUrl());
				promotionInfoBO.setSkuName(skuInfo.getName());
				
				//3. 查询阶梯促销信息
				List<LadderPromotionInfoBO> ladderPromotionInfoBOList = new ArrayList<LadderPromotionInfoBO>();
				
				//3.1 根据促销id查询阶梯规则列表
				LadderPromotionDAO ladderPromotionDAO = new LadderPromotionDAO();
				ladderPromotionDAO.setPromotionId(promotionBO.getId());
				List<LadderPromotionDAO> ladderDAOList = iLadderPromotion.queryLadderPromoitonByCondition(ladderPromotionDAO);
				List<Long> ladderIdList =  new ArrayList<Long>();
				for(LadderPromotionDAO ladderPromotionRelationDAO : ladderDAOList){
					ladderIdList.add(ladderPromotionRelationDAO.getLadderId());
				}
				//3.2 查询阶梯类型列表
				List<LadderDAO> ladderList = iLadderDAO.queryLadderList(ladderIdList);
				for(LadderDAO ladderDAO : ladderList){
					LadderPromotionInfoBO ladderPromotionInfoBO = new LadderPromotionInfoBO();
					//设置折扣度
					ladderPromotionInfoBO.setDiscount(ladderDAO.getPriceDiscount().divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_DOWN).toString());
					//设置折扣价格
					ladderPromotionInfoBO.setPromotionPrice(ladderDAO.getPriceDiscount().divide(new BigDecimal("100")).multiply(skuInfo.getOutprice()));
					//设置该阶梯促销的数量
					ladderPromotionInfoBO.setSkuNum(ladderDAO.getNumPercent().divide(new BigDecimal("100")).multiply(new BigDecimal(promotionBO.getSkuNum())).intValue());
					ladderDAO.getType();
					ladderPromotionInfoBOList.add(ladderPromotionInfoBO);
				}
				
				promotionInfoBO.setLadderPromotionList(ladderPromotionInfoBOList);
				promotionInfoBOList.add(promotionInfoBO);
				String promotionStr = JsonUtil.toJson(promotionInfoBO);
				iPromotionRedis.setValue(RedisKeyPrefixConstant.PROMOTION_INDEX+promotionBO.getId(), promotionStr, 60* 60 * 30);
			}
			
		}catch(Exception e){
		}
		
		return promotionInfoBOList;
	}
	
	public void refrashPromotion(){
		
	}

	public void setiPromotionDAO(IPromotionDAO iPromotionDAO) {
		this.iPromotionDAO = iPromotionDAO;
	}


	public void setiLadderDAO(ILadderDAO iLadderDAO) {
		this.iLadderDAO = iLadderDAO;
	}


	public void setiSkuDAO(ISkuDAO iSkuDAO) {
		this.iSkuDAO = iSkuDAO;
	}


	public void setiPromotionSequenceDAO(IPromotionSequenceDAO iPromotionSequenceDAO) {
		this.iPromotionSequenceDAO = iPromotionSequenceDAO;
	}


	public void setiLadderPromotion(ILadderPromotionDAO iLadderPromotion) {
		this.iLadderPromotion = iLadderPromotion;
	}


	public void setiPromotionRedis(IPromotionRedis iPromotionRedis) {
		this.iPromotionRedis = iPromotionRedis;
	}

}
