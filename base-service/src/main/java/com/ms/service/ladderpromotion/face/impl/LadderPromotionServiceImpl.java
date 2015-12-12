package com.ms.service.ladderpromotion.face.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.dao.ladderpromotion.face.ILadderPromotionDAO;
import com.ms.dao.promotion.redis.IPromotionRedis;
import com.ms.domain.convert.LadderPromotionConvert;
import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.ladderpromotion.face.ILadderPromotionService;

public class LadderPromotionServiceImpl implements ILadderPromotionService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private ILadderPromotionDAO iLadderPromotionDAO;
	
	//操作redis的方法
	private IPromotionRedis iPromotionRedis;

	public List<LadderPromotionBO> queryLadderPromotionByPromotionId(long promotionId) {
		List<LadderPromotionBO> ladderPromotionBOList = new ArrayList<LadderPromotionBO>();
		try{
			String ladderPromotionRedisStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.LADDER_PROMOTION_PRIFIXE+String.valueOf(promotionId));
			if(StringUtils.isNotBlank(ladderPromotionRedisStr)){
				ladderPromotionBOList = JsonUtil.readJson(ladderPromotionRedisStr, List.class, LadderPromotionBO.class);
			}
			if(CollectionUtils.isNotEmpty(ladderPromotionBOList)){
				return ladderPromotionBOList;
			}else{
				ladderPromotionBOList = queryLadderPromotionFromDBById(promotionId);
				ladderPromotionRedisStr = JsonUtil.toJson(ladderPromotionBOList);
				iPromotionRedis.setValue(RedisKeyPrefixConstant.LADDER_PROMOTION_PRIFIXE+String.valueOf(promotionId), ladderPromotionRedisStr, RedisKeyPrefixConstant.LADDER_PROMOTION_TIME);
				return ladderPromotionBOList;
			}
		}catch(Exception e){
			logger.error("LadderPromotionServiceImpl.queryLadderPromotionByPromotionId查询阶梯促销信息过程中发生异常！！！", e);
		}
		return null;
	}

	public Map<Long, List<LadderPromotionBO>> queryLadderPromotionByPromotionIds(List<Long> ladderPromotionIds) {
		try{
			Map<Long, List<LadderPromotionBO>> ladderPromotionMap = new HashMap<Long, List<LadderPromotionBO>>();
			if(CollectionUtils.isEmpty(ladderPromotionIds)){
				return ladderPromotionMap;
			}
			for(long ladderPromotionId : ladderPromotionIds){
				List<LadderPromotionBO> ladderPromotionBOList = queryLadderPromotionByPromotionId(ladderPromotionId);
				ladderPromotionMap.put(ladderPromotionId, ladderPromotionBOList);
			}
			return ladderPromotionMap;
		}catch(Exception e){
			logger.error("LadderPromotionServiceImpl.queryLadderPromotionByPromotionIds查询阶梯促销信息过程中发生异常！！！", e);
		}
		return null;
		
	}

	/**
	 * 从数据库中查询单个规则
	 * @param skuId
	 * @return
	 */
	private List<LadderPromotionBO> queryLadderPromotionFromDBById(long ladderPromotionId){
		try {
			LadderPromotionDAO ladderPromotionDAO = new LadderPromotionDAO();
			ladderPromotionDAO.setLadderId(ladderPromotionId);
			List<LadderPromotionDAO> ladderPromotionFromDBList = iLadderPromotionDAO.queryLadderPromoitonByCondition(ladderPromotionDAO);
			List<LadderPromotionBO> ladderPromotionBOList = LadderPromotionConvert.convertDAOTOBOList(ladderPromotionFromDBList);
			return ladderPromotionBOList;
		} catch (Exception e) {
			logger.error("LadderPromotionServiceImpl.queryLadderPromotionFromDBById从数据库中查询规则信息时发生异常！！！", e);
		}
		return null;
	}
	
	/**
	 * 从数据库中批量查询多个规则
	 * @param skuId
	 * @return
	 */
	private Map<Long, List<LadderPromotionBO>> queryLadderPromotionFromDBByIds(List<Long> ladderPromotionIds){
		try {
			Map<Long, List<LadderPromotionBO>> ladderPromotionMap = new HashMap<Long, List<LadderPromotionBO>>();
			for(long ladderPromotionId : ladderPromotionIds){
				List<LadderPromotionBO> ladderPromotionBOList = queryLadderPromotionFromDBById(ladderPromotionId);
				ladderPromotionMap.put(ladderPromotionId, ladderPromotionBOList);
			}
			return ladderPromotionMap;
		} catch (Exception e) {
			logger.error("LadderPromotionServiceImpl.queryLadderPromotionFromDBByIds从数据库中查询规则信息时发生异常！！！", e);
		}
		return null;
	}
	public void setiLadderPromotionDAO(ILadderPromotionDAO iLadderPromotionDAO) {
		this.iLadderPromotionDAO = iLadderPromotionDAO;
	}

	public void setiPromotionRedis(IPromotionRedis iPromotionRedis) {
		this.iPromotionRedis = iPromotionRedis;
	}

}
