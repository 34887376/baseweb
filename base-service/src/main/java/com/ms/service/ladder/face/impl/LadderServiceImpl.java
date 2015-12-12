package com.ms.service.ladder.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.dao.ladder.face.ILadderDAO;
import com.ms.dao.promotion.redis.IPromotionRedis;
import com.ms.domain.convert.LadderConvert;
import com.ms.domain.ladder.bo.LadderBO;
import com.ms.domain.ladder.dao.LadderDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.ladder.face.ILadderService;

public class LadderServiceImpl implements ILadderService {

	private Logger logger = Logger.getLogger(this.getClass());
	
	//规则信息查询数据库接口
	private ILadderDAO iLadderDAO;
	
	//操作redis的方法
	private IPromotionRedis iPromotionRedis;

	public LadderBO queryLadderById(long ladderId) {
		LadderBO ladderBO = new LadderBO();
		try{
			String ladderRedisStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.LADDER_PRIFIXE+String.valueOf(ladderId));
			if(StringUtils.isNotBlank(ladderRedisStr)){
				ladderBO = JsonUtil.fromJson(ladderRedisStr, LadderBO.class);
			}
			if(ladderBO==null){
				ladderBO = queryLadderFromDBById(ladderId);
				ladderRedisStr = JsonUtil.toJson(ladderBO);
				iPromotionRedis.setValue(RedisKeyPrefixConstant.LADDER_PRIFIXE+String.valueOf(ladderId), ladderRedisStr, RedisKeyPrefixConstant.LADDER_TIME);
			}
			return ladderBO;
		}catch(Exception e){
			logger.error("LadderServiceImpl.queryLadderById查询规则信息过程中发生异常！！！", e);
		}
		return null;
	}

	public List<LadderBO> queryLadderByIds(List<Long> ladderIds) {
		List<LadderBO> ladderList = new ArrayList<LadderBO>();
		try{
			if(CollectionUtils.isEmpty(ladderIds)){
				return ladderList;
			}
			List<Long> ladderIdForDAOList = new ArrayList<Long>();
			//先从redis中获取数据
			for(Long ladderId : ladderIds){
				LadderBO ladderBO = null;
				String ladderRedisStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.LADDER_PRIFIXE+String.valueOf(ladderId));
				if(StringUtils.isNotBlank(ladderRedisStr)){
					ladderBO = JsonUtil.fromJson(ladderRedisStr, LadderBO.class);
				}
				//如果从redis拿不到则记录下，准备去数据库中获取
				if(ladderBO==null){
					ladderIdForDAOList.add(ladderId);
				}else{
					ladderList.add(ladderBO);
				}
			}
			
			if(CollectionUtils.isEmpty(ladderIdForDAOList)){
				return ladderList;
			}
			
			//然后查询数据库
			List<LadderBO> ladderBOFromDBList = queryLadderFromDBByIds(ladderIdForDAOList);
			if(CollectionUtils.isEmpty(ladderBOFromDBList)){
				return ladderList;
			}
			for(LadderBO ladderBOFromDB : ladderBOFromDBList){
				if(ladderBOFromDB!=null){
					ladderList.add(ladderBOFromDB);
					String ladderStrFromDB = JsonUtil.toJson(ladderBOFromDB);
					iPromotionRedis.setValue(RedisKeyPrefixConstant.LADDER_PRIFIXE+String.valueOf(ladderBOFromDB.getId()), ladderStrFromDB, RedisKeyPrefixConstant.LADDER_TIME);
				}
			}
		}catch(Exception e){
			logger.error("LadderServiceImpl.queryLadderByIds查询规则信息数据过程中发生异常！！！", e);
		}
		return ladderList;
	}
	
	/**
	 * 从数据库中查询单个规则
	 * @param skuId
	 * @return
	 */
	private LadderBO queryLadderFromDBById(long ladderId){
		try {
			LadderDAO ladderDAO = iLadderDAO.queryLadderById(ladderId);
			LadderBO ladderBO = LadderConvert.convertDAOTOBO(ladderDAO);
			return ladderBO;
		} catch (Exception e) {
			logger.error("LadderServiceImpl.queryLadderFromDBById从数据库中查询规则信息时发生异常！！！", e);
		}
		return null;
	}
	
	/**
	 * 从数据库中批量查询多个规则
	 * @param skuId
	 * @return
	 */
	private List<LadderBO> queryLadderFromDBByIds(List<Long> ladderIds){
		try {
			List<LadderDAO> ladderDAOList = iLadderDAO.queryLadderList(ladderIds);
			List<LadderBO> ladderBOList = LadderConvert.convertDAOTOBOList(ladderDAOList);
			return ladderBOList;
		} catch (Exception e) {
			logger.error("SkuServiceImpl.queryLadderFromDBByIds从数据库中查询规则信息时发生异常！！！", e);
		}
		return null;
	}
	
	public void setiLadderDAO(ILadderDAO iLadderDAO) {
		this.iLadderDAO = iLadderDAO;
	}

	public void setiPromotionRedis(IPromotionRedis iPromotionRedis) {
		this.iPromotionRedis = iPromotionRedis;
	}
}
