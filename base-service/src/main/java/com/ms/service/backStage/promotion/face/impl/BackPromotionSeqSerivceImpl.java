package com.ms.service.backStage.promotion.face.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.dao.ladder.face.ILadderDAO;
import com.ms.dao.ladderpromotion.face.ILadderPromotionDAO;
import com.ms.dao.promotion.face.IPromotionDAO;
import com.ms.dao.promotion.redis.IPromotionRedis;
import com.ms.dao.promotionsequence.face.IPromotionSequenceDAO;
import com.ms.dao.sku.face.ISkuDAO;
import com.ms.domain.convert.PromotionSequenceConvert;
import com.ms.domain.ladder.dao.LadderDAO;
import com.ms.domain.ladderpromotion.bo.LadderPromotionInfoBO;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;
import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.bo.PromotionDetailInfoBO;
import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;
import com.ms.domain.promotionsequence.dao.PromotionSequenceDAO;
import com.ms.domain.sku.dao.SkuDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.backStage.promotion.face.IBackPromotionSeqSerivce;
import com.ms.service.promotion.face.IPromotionService;

/**
 *  后台信息管理
 * @author zhoushanjie
 *
 */
public class BackPromotionSeqSerivceImpl implements IBackPromotionSeqSerivce {

	
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
	
	//查询促销信息
	private IPromotionService iPromotionService;
	
	private static final String startPromotionIndexKey=RedisKeyPrefixConstant.START_PROMOTION_INDEX;
	private static final String promotionKey=RedisKeyPrefixConstant.PROMOTION_INFO_INDEX;
	
	public boolean refreshBackPromotionInfoToRedis(long startPromotionId) {

		iPromotionRedis.setValue(startPromotionIndexKey, String.valueOf(startPromotionId));

		try {
			PromotionSequenceDAO promotionSequenceDAO = new PromotionSequenceDAO();
			promotionSequenceDAO.setPromotionId(startPromotionId);
			List<PromotionSequenceDAO> promotionInfo = iPromotionSequenceDAO.queryPromotionSequenceByCondition(promotionSequenceDAO);
			//循环查询数据库中的促销信息,直到查不到信息为止
			while(true){
					//查不到信息则结束
					if(promotionInfo == null || promotionInfo.get(0)==null || promotionInfo.get(0).getNextOrder()==null){
						break;
					}
					String promotionId = String.valueOf(promotionInfo.get(0).getPromotionId());
					String promotionInfoStr = JsonUtil.toJson(promotionInfo.get(0));
					iPromotionRedis.setValue(promotionKey+promotionId, promotionInfoStr,RedisKeyPrefixConstant.PROMOTION_INFO_INDEX_TIME);
					iPromotionRedis.setValue(startPromotionIndexKey+startPromotionId,  String.valueOf(promotionId), RedisKeyPrefixConstant.START_PROMOTION_TIME);
					
					long nextOrderId = promotionInfo.get(0).getNextOrder();
					PromotionSequenceDAO nextPromotionSequenceDAO = new PromotionSequenceDAO();
					nextPromotionSequenceDAO.setId(nextOrderId);
					promotionInfo = iPromotionSequenceDAO.queryPromotionSequenceByCondition(nextPromotionSequenceDAO);
					startPromotionId = promotionInfo.get(0).getPromotionId();
			}
			return true;
		} catch (Exception e) {
			logger.error("BackPromotionSeqSerivceImpl.refreshBackPromotionInfoToRedis往redis中刷新数据异常！！！", e);
			return false;
		}
	}
	

	public void deletePromotionFromRedis() {
		
	}

	/**
	 * 将一条记录设置为无效
	 * @param promotionId
	 */
	public void deletePromotionSequence(long promotionId) {

		try {
			PromotionSequenceDAO promotionSequenceDAO = new PromotionSequenceDAO();
			promotionSequenceDAO.setPromotionId(promotionId);
			List<PromotionSequenceDAO> promotionInfo = iPromotionSequenceDAO.queryPromotionSequenceByCondition(promotionSequenceDAO);
			
			//当前要删除记录的前序节点和后序节点的id
			long previousOrderId = promotionInfo.get(0).getPreviosOrder();
			long nextOrderId = promotionInfo.get(0).getNextOrder();
			
			
			//当前要删除记录的前序节点和后序节点的内容
			PromotionSequenceDAO previosPromotionSequenceDAO = new PromotionSequenceDAO();
			previosPromotionSequenceDAO.setId(previousOrderId);
			List<PromotionSequenceDAO> previousPromotionInfo = iPromotionSequenceDAO.queryPromotionSequenceByCondition(previosPromotionSequenceDAO);
			long previosPromotionId = previousPromotionInfo.get(0).getPromotionId();
			
			PromotionSequenceDAO nextPromotionSequenceDAO = new PromotionSequenceDAO();
			List<PromotionSequenceDAO> nextPromotionInfo = iPromotionSequenceDAO.queryPromotionSequenceByCondition(nextPromotionSequenceDAO);
			long nextPromotionId = nextPromotionInfo.get(0).getPromotionId();

			
			/**
			 * 更改redis关系
			 */
			//更改要删除节点在redis中的前序关系---将本节点的下一个节点的关系移动到前一个节点上
			if(StringUtils.isNotBlank(iPromotionRedis.getValue(promotionKey+previosPromotionId))){
				iPromotionRedis.setValue(startPromotionIndexKey+previosPromotionId, String.valueOf(nextPromotionId), RedisKeyPrefixConstant.START_PROMOTION_TIME);
			}
			//更改要删除节点在redis中的后序关系---直接删除本节点
			if(StringUtils.isNotBlank(iPromotionRedis.getValue(promotionKey+promotionId))){
				iPromotionRedis.delValue(startPromotionIndexKey+promotionId);
			}
			
			/**
			 * 更改db关系
			 */
			//更新前序关系
			PromotionSequenceDAO newPreviousPromotionSequenceDAO = new PromotionSequenceDAO();
			
			newPreviousPromotionSequenceDAO.setNextOrder(nextPromotionId);
			newPreviousPromotionSequenceDAO.setId(previousOrderId);
			iPromotionSequenceDAO.updatePromotionSequence(newPreviousPromotionSequenceDAO);
			
			//更新后序关系
			PromotionSequenceDAO newNextPromotionSequenceDAO = new PromotionSequenceDAO();
			newNextPromotionSequenceDAO.setPreviosOrder(previosPromotionId);
			newNextPromotionSequenceDAO.setId(nextOrderId);
			iPromotionSequenceDAO.updatePromotionSequence(newNextPromotionSequenceDAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recoverPromotionSequence(long promotionId, long previousOrderId, long nextOrderId){
		PromotionSequenceDAO promotionSequenceDAO = new PromotionSequenceDAO();
		promotionSequenceDAO.setId(promotionId);
		promotionSequenceDAO.setPreviosOrder(previousOrderId);
		promotionSequenceDAO.setNextOrder(nextOrderId);
		try {
			iPromotionSequenceDAO.updatePromotionSequence(promotionSequenceDAO);
			
			List<PromotionSequenceDAO> promotionInfo = iPromotionSequenceDAO.queryPromotionSequenceByCondition(promotionSequenceDAO);
			String promotionInfoStr = JsonUtil.toJson(promotionInfo.get(0));
			iPromotionRedis.setValue(promotionKey+promotionId, promotionInfoStr);
			
			iPromotionRedis.setValue(startPromotionIndexKey+previousOrderId, String.valueOf(promotionId));
			iPromotionRedis.setValue(startPromotionIndexKey+promotionId, String.valueOf(nextOrderId));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void refreshPromotionSequence(){
		
	}
	
	
	
	/**
	 * 从数据库中查询促销信息，并将热点促销信息刷入到缓存中
	 * @param promotionIdList
	 * @return
	 */
	private List<PromotionDetailInfoBO> getPromotionInfoFromDB(List<Long> promotionIdList){
		
		List<PromotionDetailInfoBO> promotionInfoBOList = new ArrayList<PromotionDetailInfoBO>();
		try{
			
			//1. 查询促销列表
			List<PromotionBO> promotionBOList = iPromotionService.queryPromotionsByIds(promotionIdList);
			for(PromotionBO promotionBO : promotionBOList){
				PromotionDetailInfoBO promotionInfoBO = new PromotionDetailInfoBO();
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
				iPromotionRedis.setValue(RedisKeyPrefixConstant.PROMOTION_INFO_INDEX+promotionBO.getId(), promotionStr, 60* 60 * 30);
			}
			
		}catch(Exception e){
		}
		
		return promotionInfoBOList;
	}
	
	
	public List<PromotionSequenceBO> queryPromotionSequenceByPageNum(int page,
			int pageSize) {
		List<PromotionSequenceBO> promotionSeqBOList = new ArrayList<PromotionSequenceBO>();
		try{
			List<PromotionSequenceDAO> promotionSeqDAOList = iPromotionSequenceDAO.queryPromotionSequenceByPageNum(page, pageSize);
			promotionSeqBOList = PromotionSequenceConvert.convertDAOTOBOList(promotionSeqDAOList);
		}catch(Exception e){
			logger.error("BackPromotionSeqSerivceImpl.queryPromotionSequenceByPageNum查询促销信息时发生异常，入参{page="+page+"}", e);
		}
		return promotionSeqBOList;
	}


	public List<PromotionSequenceBO> queryPromotionSequenceByCondition(
			PromotionSequenceBO promotionSequenceBO) {
		List<PromotionSequenceBO> promotionSeqBOList = new ArrayList<PromotionSequenceBO>();
		try{
			PromotionSequenceDAO promotionSequenceDAO = PromotionSequenceConvert.convertBOTODAO(promotionSequenceBO);
			List<PromotionSequenceDAO> promotionSeqDAOList = iPromotionSequenceDAO.queryPromotionSequenceByCondition(promotionSequenceDAO );
			promotionSeqBOList = PromotionSequenceConvert.convertDAOTOBOList(promotionSeqDAOList);
		}catch(Exception e){
			logger.error("BackPromotionSeqSerivceImpl.queryPromotionSequenceByCondition查询促销信息时发生异常，入参{promotionSequenceBO="+JsonUtil.toJson(promotionSequenceBO)+"}", e);
		}
		return promotionSeqBOList;
	}


	public boolean addPromotionSequence(PromotionSequenceBO promotionSequenceBO) {
		try{
			PromotionSequenceDAO promotionSequenceDAO = PromotionSequenceConvert.convertBOTODAO(promotionSequenceBO);
			long promotionSeqId = iPromotionSequenceDAO.addPromotionSequence(promotionSequenceDAO);
			if(promotionSeqId>0){
				return true;
			}
		}catch(Exception e){
			logger.error("BackPromotionSeqSerivceImpl.addPromotionSequence添加促销信息时发生异常，入参{promotionSequenceBO="+JsonUtil.toJson(promotionSequenceBO)+"}", e);
		}
		return false;
	}

	public boolean delPromotionSequence(List<Long> idList) {
		try{
			boolean delResult = iPromotionSequenceDAO.delPromotionSequence(idList);
			if(delResult){
				return true;
			}
		}catch(Exception e){
			logger.error("BackPromotionSeqSerivceImpl.delPromotionSequence删除促销信息时发生异常，入参{idList="+JsonUtil.toJson(idList)+"}", e);
		}
		return false;
	}


	public boolean updatePromotionSequence(PromotionSequenceBO promotionSequenceBO) {
		try{
			PromotionSequenceDAO promotionSequenceDAO = PromotionSequenceConvert.convertBOTODAO(promotionSequenceBO);
			boolean updateResult = iPromotionSequenceDAO.updatePromotionSequence(promotionSequenceDAO);
			if(updateResult){
				return true;
			}
		}catch(Exception e){
			logger.error("BackPromotionSeqSerivceImpl.delPromotionSequence更新促销信息时发生异常，入参{promotionSequenceBO="+JsonUtil.toJson(promotionSequenceBO)+"}", e);
		}
		return false;
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
