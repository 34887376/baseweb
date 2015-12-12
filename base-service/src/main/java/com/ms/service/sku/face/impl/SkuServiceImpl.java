package com.ms.service.sku.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.dao.promotion.redis.IPromotionRedis;
import com.ms.dao.sku.face.ISkuDAO;
import com.ms.domain.convert.SkuConvert;
import com.ms.domain.sku.bo.SkuBO;
import com.ms.domain.sku.dao.SkuDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.sku.face.ISkuService;

public class SkuServiceImpl implements ISkuService {

	private Logger logger = Logger.getLogger(this.getClass());
	
	//sku查询数据库接口
	private ISkuDAO iSkuDAO;
	
	//操作redis的方法
	private IPromotionRedis iPromotionRedis;
	
	
	public SkuBO querySkuById(long skuId) {
		SkuBO skuBO = new SkuBO();
		try{
			String skuRedisStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.SKU_PRIFIXE+String.valueOf(skuId));
			if(StringUtils.isNotBlank(skuRedisStr)){
				skuBO = JsonUtil.fromJson(skuRedisStr, SkuBO.class);
			}
			if(skuBO==null){
				skuBO = querySkuFromDBById(skuId);
				skuRedisStr = JsonUtil.toJson(skuBO);
				iPromotionRedis.setValue(RedisKeyPrefixConstant.SKU_PRIFIXE+String.valueOf(skuId), skuRedisStr, RedisKeyPrefixConstant.SKU_TIME);
			}
			return skuBO;
		}catch(Exception e){
			logger.error("SkuServiceImpl.querySkuById查询商品数据过程中发生异常！！！", e);
		}
		return null;
	}

	public List<SkuBO> querySkuByIds(List<Long> skuIdList) {
		List<SkuBO> skuList = new ArrayList<SkuBO>();
		try{
			if(CollectionUtils.isEmpty(skuIdList)){
				return skuList;
			}
			List<Long> skuIdForDAOList = new ArrayList<Long>();
			//先从redis中获取数据
			for(Long skuId : skuIdList){
				SkuBO skuBO = null;
				String skuRedisStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.SKU_PRIFIXE+String.valueOf(skuId));
				if(StringUtils.isNotBlank(skuRedisStr)){
					skuBO = JsonUtil.fromJson(skuRedisStr, SkuBO.class);
				}
				//如果从redis拿不到则记录下，准备去数据库中获取
				if(skuBO==null){
					skuIdForDAOList.add(skuId);
				}else{
					skuList.add(skuBO);
				}
			}
			
			if(CollectionUtils.isEmpty(skuIdForDAOList)){
				return skuList;
			}
			
			//然后查询数据库
			List<SkuBO> skuBOFromDBList = querySkuFromDBByIds(skuIdForDAOList);
			if(CollectionUtils.isEmpty(skuBOFromDBList)){
				return skuList;
			}
			for(SkuBO skuBOFromDB : skuBOFromDBList){
				if(skuBOFromDB!=null){
					skuList.add(skuBOFromDB);
					String skuStrFromDB = JsonUtil.toJson(skuBOFromDB);
					iPromotionRedis.setValue(RedisKeyPrefixConstant.SKU_PRIFIXE+String.valueOf(skuBOFromDB.getId()), skuStrFromDB, RedisKeyPrefixConstant.SKU_TIME);
				}
			}
		}catch(Exception e){
			logger.error("SkuServiceImpl.querySkuByIds查询商品数据过程中发生异常！！！", e);
		}
		return skuList;
	}
	
	/**
	 * 从数据库中查询单个商品
	 * @param skuId
	 * @return
	 */
	private SkuBO querySkuFromDBById(long skuId){
		try {
			SkuDAO skuDAO = iSkuDAO.querySkuById(skuId);
			SkuBO skuBO = SkuConvert.convertDAOTOBO(skuDAO);
			return skuBO;
		} catch (Exception e) {
			logger.error("SkuServiceImpl.querySkuFromDB查询数据库过程中发生异常！！！", e);
		}
		return null;
	}
	
	/**
	 * 从数据库中批量查询多个商品
	 * @param skuId
	 * @return
	 */
	private List<SkuBO> querySkuFromDBByIds(List<Long> skuIds){
		try {
			List<SkuDAO> skuDAOList = iSkuDAO.querySkuListByIds(skuIds);
			List<SkuBO> skuBOList = SkuConvert.convertDAOTOBOList(skuDAOList);
			return skuBOList;
		} catch (Exception e) {
			logger.error("SkuServiceImpl.querySkuFromDBByIds查询数据库过程中发生异常！！！", e);
		}
		return null;
	}

	public void setiSkuDAO(ISkuDAO iSkuDAO) {
		this.iSkuDAO = iSkuDAO;
	}

	public void setiPromotionRedis(IPromotionRedis iPromotionRedis) {
		this.iPromotionRedis = iPromotionRedis;
	}

}
