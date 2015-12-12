package com.ms.service.promotionsequence.face.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.dao.promotion.redis.IPromotionRedis;
import com.ms.dao.promotionsequence.face.IPromotionSequenceDAO;
import com.ms.domain.convert.PromotionSequenceConvert;
import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;
import com.ms.domain.promotionsequence.dao.PromotionSequenceDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.promotionsequence.face.IPromotionSequenceService;

public class PromotionSequenceServiceImpl implements IPromotionSequenceService {
	
	private Logger logger = Logger.getLogger(this.getClass());

	//促销序列DAO管理接口
	private IPromotionSequenceDAO iPromotionSequenceDAO;
	
	//操作redis的方法
	private IPromotionRedis iPromotionRedis;
	
	public PromotionSequenceBO queryPromotionSequenceByPromotionId(long promotionId) {
		PromotionSequenceBO promotionSeqBO = new PromotionSequenceBO();
		
		try{
			String promotionSequenceStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.PROMOTION_SEQUENCE_PROMOTIONID_PRIFIXE+String.valueOf(promotionId));
			if(StringUtils.isNotBlank(promotionSequenceStr)){
				promotionSeqBO = JsonUtil.fromJson(promotionSequenceStr, PromotionSequenceBO.class);
				return promotionSeqBO;
			}
			PromotionSequenceBO promotionSequenceBO = new PromotionSequenceBO();
			promotionSequenceBO.setPromotionId(promotionId);
			PromotionSequenceDAO promotionSequenceDAO = PromotionSequenceConvert.convertBOTODAO(promotionSequenceBO);
			List<PromotionSequenceDAO> promotionSeqDAOList = iPromotionSequenceDAO.queryPromotionSequenceByCondition(promotionSequenceDAO );
			List<PromotionSequenceBO> promotionSeqBOList = PromotionSequenceConvert.convertDAOTOBOList(promotionSeqDAOList);
			PromotionSequenceBO promotionSeqBOFromDB = promotionSeqBOList.get(0);
			String promotionSeqStr = JsonUtil.toJson(promotionSeqBOFromDB);
			iPromotionRedis.setValue(RedisKeyPrefixConstant.PROMOTION_SEQUENCE_PROMOTIONID_PRIFIXE+String.valueOf(promotionId), promotionSeqStr, RedisKeyPrefixConstant.PROMOTION_SEQUENCE_PROMOTIONID_TIME);
			return promotionSeqBOFromDB;
		}catch(Exception e){
			logger.error("PromotionSequenceServiceImpl.queryPromotionSequenceByCondition查询促销信息时发生异常，入参{promotionId="+JsonUtil.toJson(promotionId)+"}", e);
		}
		return null;
	}

	public PromotionSequenceBO queryPromotionSequenceById(long promotionSequenceId) {
		PromotionSequenceBO promotionSeqBO = new PromotionSequenceBO();
		try{
			String promotionSequenceStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.PROMOTION_SEQUENCE_ID_PRIFIXE+String.valueOf(promotionSequenceId));
			if(StringUtils.isNotBlank(promotionSequenceStr)){
				promotionSeqBO = JsonUtil.fromJson(promotionSequenceStr, PromotionSequenceBO.class);
				return promotionSeqBO;
			}
			PromotionSequenceBO promotionSequenceBO = new PromotionSequenceBO();
			promotionSequenceBO.setId(promotionSequenceId);
			PromotionSequenceDAO promotionSequenceDAO = PromotionSequenceConvert.convertBOTODAO(promotionSequenceBO);
			List<PromotionSequenceDAO> promotionSeqDAOList = iPromotionSequenceDAO.queryPromotionSequenceByCondition(promotionSequenceDAO);
			List<PromotionSequenceBO> promotionSeqBOList = PromotionSequenceConvert.convertDAOTOBOList(promotionSeqDAOList);
			PromotionSequenceBO promotionSeqBOFromDB = promotionSeqBOList.get(0);
			String promotionSeqStr = JsonUtil.toJson(promotionSeqBOFromDB);
			iPromotionRedis.setValue(RedisKeyPrefixConstant.PROMOTION_SEQUENCE_ID_PRIFIXE+String.valueOf(promotionSequenceId), promotionSeqStr, RedisKeyPrefixConstant.PROMOTION_SEQUENCE_ID_TIME);
			return promotionSeqBOFromDB;
		}catch(Exception e){
			logger.error("PromotionSequenceServiceImpl.queryPromotionSequenceById查询促销信息时发生异常，入参{promotionId="+JsonUtil.toJson(promotionSequenceId)+"}", e);
		}
		return null;
	}

}
