package com.ms.service.promotion.face.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.domain.convert.PromotionConvert;
import com.ms.domain.ladder.bo.LadderBO;
import com.ms.domain.ladder.dao.LadderDAO;
import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;
import com.ms.domain.ladderpromotion.bo.LadderPromotionInfoBO;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;
import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.bo.PromotionBriefBOInfo;
import com.ms.domain.promotion.bo.PromotionDetailInfoBO;
import com.ms.domain.promotion.dao.PromotionDAO;
import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;
import com.ms.domain.promotionsequence.dao.PromotionSequenceDAO;
import com.ms.domain.sku.bo.SkuBO;
import com.ms.domain.sku.dao.SkuDAO;
import com.ms.redis.constant.RedisKeyPrefixConstant;
import com.ms.service.ladder.face.ILadderService;
import com.ms.service.ladderpromotion.face.ILadderPromotionService;
import com.ms.service.promotion.face.IPromotionService;
import com.ms.service.promotionsequence.face.IPromotionSequenceService;
import com.ms.service.sku.face.ISkuService;
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
	
	//促销序列service
	private IPromotionSequenceService iPromotionSequenceService;
	
	//商品信息service
	private ISkuService iSkuService;
	
	//阶梯促销规则service
	private ILadderPromotionService iLadderPromotionService;
	
	//阶梯规则信息service
	private ILadderService iLadderService;
	

	public PromotionBO queryPromotionById(Long promotionId) {
		PromotionBO promotionBO =new PromotionBO();
		try {
			String promotionRedisStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.LADDER_PRIFIXE+String.valueOf(promotionId));
			if(StringUtils.isNotBlank(promotionRedisStr)){
				promotionBO = JsonUtil.fromJson(promotionRedisStr, PromotionBO.class);
			}
			if(promotionBO==null){
				promotionBO = queryPromotionByIdFromDB(promotionId);
				promotionRedisStr = JsonUtil.toJson(promotionBO);
				iPromotionRedis.setValue(RedisKeyPrefixConstant.PROMOTION_PREFIXE+String.valueOf(promotionId), promotionRedisStr, RedisKeyPrefixConstant.PROMOTION_PREFIXE_TIME);
			}
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.queryPromotionById查询促销信息时发生异常，入参{id="+promotionId+"}", e);
		}
		return promotionBO;
	}

	public List<PromotionBO> queryPromotionsByIds(List<Long> promotionIds) {
		List<PromotionBO> promotionList = new ArrayList<PromotionBO>();
		try {
			if(CollectionUtils.isEmpty(promotionIds)){
				return promotionList;
			}
			List<Long> promotionIdForDAOList = new ArrayList<Long>();
			//先从redis中获取数据
			for(Long promotionId : promotionIds){
				PromotionBO promotionBO = null;
				String promotionRedisStr = iPromotionRedis.getValue(RedisKeyPrefixConstant.PROMOTION_PREFIXE+String.valueOf(promotionId));
				if(StringUtils.isNotBlank(promotionRedisStr)){
					promotionBO = JsonUtil.fromJson(promotionRedisStr, PromotionBO.class);
				}
				//如果从redis拿不到则记录下，准备去数据库中获取
				if(promotionBO==null){
					promotionIdForDAOList.add(promotionId);
				}else{
					promotionList.add(promotionBO);
				}
			}
			
			if(CollectionUtils.isEmpty(promotionIdForDAOList)){
				return promotionList;
			}
			
			//然后查询数据库
			List<PromotionBO> promotionBOFromDBList = queryPromotionsByIdsFromDB(promotionIdForDAOList);
			if(CollectionUtils.isEmpty(promotionBOFromDBList)){
				return promotionList;
			}
			for(PromotionBO promotionBOFromDB : promotionBOFromDBList){
				if(promotionBOFromDB!=null){
					promotionList.add(promotionBOFromDB);
					String promoitonStrFromDB = JsonUtil.toJson(promotionBOFromDB);
					iPromotionRedis.setValue(RedisKeyPrefixConstant.PROMOTION_PREFIXE+String.valueOf(promotionBOFromDB.getId()), promoitonStrFromDB, RedisKeyPrefixConstant.PROMOTION_PREFIXE_TIME);
				}
			}
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.queryPromotionsByIds查询促销信息时发生异常，入参{promotionIds="+JsonUtil.toJson(promotionIds)+"}", e);
		}
		return promotionList;
	}
	
	
	
	private PromotionBO queryPromotionByIdFromDB(Long id) {
		PromotionBO promotionBO =new PromotionBO();
		try {
			PromotionDAO promotionDAO = iPromotionDAO.queryPromotionById(id);
			promotionBO = PromotionConvert.convertDAOTOBO(promotionDAO);
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.queryPromotionById查询促销信息时发生异常，入参{id="+id+"}", e);
		}
		return promotionBO;
	}

	private List<PromotionBO> queryPromotionsByIdsFromDB(List<Long> idList) {
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

	
	public List<PromotionDetailInfoBO> queryLastNumPromotion(int num) {
		List<PromotionDetailInfoBO> promotionInfoBOList = new ArrayList<PromotionDetailInfoBO>();
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
							iPromotionRedis.setValue(startPromotionIndexKey+startPromotionId, nextPromotion.get(0).getPromotionId().toString(), RedisKeyPrefixConstant.START_PROMOTION_TIME);
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
	private List<PromotionDetailInfoBO> getPromotionInfoFromRedis(List<Long> promotionIdList, int num){
		List<PromotionDetailInfoBO> promotionInfoBOList = new ArrayList<PromotionDetailInfoBO>();
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
				promotionKeyList.add(RedisKeyPrefixConstant.PROMOTION_INFO_INDEX+promotionId);
			}
			//从redis中查询促销信息，如果查询不到，则查询数据库，并写入到redis中
			List<String> promotionInfos = iPromotionRedis.getValuesByKeyList(promotionKeyList);
			if(CollectionUtils.isNotEmpty(promotionInfos)){
				for(String promotionStr : promotionInfos){
					PromotionDetailInfoBO promotionInfoBO = JsonUtil.fromJson(promotionStr, PromotionDetailInfoBO.class);
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
	private List<PromotionDetailInfoBO> getPromotionInfoFromDB(List<Long> promotionIdList){
		
		List<PromotionDetailInfoBO> promotionInfoBOList = new ArrayList<PromotionDetailInfoBO>();
		try{
			
			//1. 查询促销列表
			List<PromotionBO> promotionBOList = this.queryPromotionsByIds(promotionIdList);
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
				iPromotionRedis.setValue(RedisKeyPrefixConstant.PROMOTION_INFO_INDEX+promotionBO.getId(), promotionStr, RedisKeyPrefixConstant.PROMOTION_INFO_INDEX_TIME);
			}
			
		}catch(Exception e){
		}
		
		return promotionInfoBOList;
	}
	
	
	public PromotionDetailInfoBO queryKillPromotionDetail(long promotionId){
		PromotionDetailInfoBO promotionDetailInfoBO = new PromotionDetailInfoBO();
		try{
			PromotionSequenceBO promotionSequence = iPromotionSequenceService.queryPromotionSequenceByPromotionId(promotionId);
			//1. 查询促销列表
			PromotionBO promotionBO = this.queryPromotionById(promotionId);
			promotionDetailInfoBO.setPromotionId(promotionId);
			promotionDetailInfoBO.setStatus(promotionSequence.getStatus());
			
			//2. 查询商品信息
			SkuBO skuBO = iSkuService.querySkuById(promotionBO.getSkuId());
			promotionDetailInfoBO.setAdvert(skuBO.getAdverst());
			promotionDetailInfoBO.setSkuName(skuBO.getName());
			
			//3. 查询阶梯促销信息
			//3.1 根据促销id查询阶梯规则列表
			Map<Long,Long> ladderIdMapLadderPromotionId = new HashMap<Long,Long>();
			List<LadderPromotionBO> ladderPromotionBOList = iLadderPromotionService.queryLadderPromotionByPromotionId(promotionBO.getId());
			List<Long> ladderIdList =  new ArrayList<Long>();
			for(LadderPromotionBO ladderPromotionBO : ladderPromotionBOList){
				ladderIdList.add(ladderPromotionBO.getLadderId());
				ladderIdMapLadderPromotionId.put(ladderPromotionBO.getLadderId(),ladderPromotionBO.getLadderPromotionId());
			}
			
			//3.2 查询阶梯规则中的最大折扣力度
			List<LadderBO> ladderBOList = iLadderService.queryLadderByIds(ladderIdList);
			List<LadderPromotionInfoBO> ladderPromotionList =  new ArrayList<LadderPromotionInfoBO>();
			for(LadderBO ladderBO : ladderBOList){
				LadderPromotionInfoBO ladderPromotionInfoBO = new LadderPromotionInfoBO();
				ladderPromotionInfoBO.setPromotionLadderId(ladderIdMapLadderPromotionId.get(ladderBO.getId()));
				//设置折扣度
				ladderPromotionInfoBO.setDiscount(ladderBO.getPriceDiscount().divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_DOWN).toString());
				//设置折扣价格
				ladderPromotionInfoBO.setPromotionPrice(ladderBO.getPriceDiscount().divide(new BigDecimal("100")).multiply(skuBO.getOutprice()));
				//设置该阶梯促销的数量
				ladderPromotionInfoBO.setSkuNum(ladderBO.getNumPercent().divide(new BigDecimal("100")).multiply(new BigDecimal(promotionBO.getSkuNum())).intValue());
				ladderBO.getType();
				ladderPromotionList.add(ladderPromotionInfoBO);
			}
			promotionDetailInfoBO.setLadderPromotionList(ladderPromotionList);
		}catch(Exception e){
			logger.error("PromotionServiceImpl.queryKillPromotion查询促销信息时发生异常，入参{promotionId="+promotionId+"}", e);
		}
		return promotionDetailInfoBO;
	}
	
	
	
	public List<PromotionBriefBOInfo> queryLastPromotionInfoForIndex(int num){
		List<PromotionBriefBOInfo> promotionBriefInfoBOList = new ArrayList<PromotionBriefBOInfo>();
		try {
			List<Long> promotionIdList = new ArrayList<Long>();
			//获取设置的第一条促销id
			String startPromotionIndexKey=RedisKeyPrefixConstant.START_PROMOTION_INDEX;
			String startPromotionId = iPromotionRedis.getValue(startPromotionIndexKey);
			if(StringUtils.isBlank(startPromotionId)){
				return promotionBriefInfoBOList;
			}
			promotionIdList.add(Long.parseLong(startPromotionId));
			//记录促销和状态的关系
			Map<Long,Integer> promotionMapStatus = new HashMap<Long,Integer>();
			//查询redis中的前num个促销信息;如果缓存中没有则查库，并写入到缓存
			for(int promotionNum=0;promotionNum<num;promotionNum++){
				//根据startPromotionId查询该促销信息
				PromotionSequenceBO promotionSequenceBO = iPromotionSequenceService.queryPromotionSequenceByPromotionId(Long.parseLong(startPromotionId));
				if(promotionSequenceBO!=null){
					promotionMapStatus.put(Long.parseLong(startPromotionId), promotionSequenceBO.getStatus());
					long nextQueryPromotionSequenceId = promotionSequenceBO.getNextOrder();
					PromotionSequenceBO nextPromotionFromDB = iPromotionSequenceService.queryPromotionSequenceById(nextQueryPromotionSequenceId);
					if(nextPromotionFromDB!=null){
						Long nextPromotionIdFromDB = nextPromotionFromDB.getPromotionId();
						promotionIdList.add(nextPromotionIdFromDB);
						startPromotionId = String.valueOf(nextPromotionIdFromDB);
					}
				}
			}
			if(CollectionUtils.isEmpty(promotionIdList)){
				return promotionBriefInfoBOList;
			}
			//从redis中查询促销信息，如果查询不到，则查询数据库，并写入到redis中
			promotionBriefInfoBOList = getPromotionBriefInfos(promotionIdList,promotionMapStatus,num);
		
		} catch (Exception e) {
			logger.error("PromotionServiceImpl.queryLastPromotionInfoForIndex查询促销简要信息时发生异常，入参{num="+JsonUtil.toJson(num)+"}", e);
		}
		return promotionBriefInfoBOList;
	}
	
	private List<PromotionBriefBOInfo> getPromotionBriefInfos(List<Long> promotionIdList, Map<Long,Integer> promotionMapStatus, int num){
		List<PromotionBriefBOInfo> promotionBriefInfoBOList = new ArrayList<PromotionBriefBOInfo>();
		try{
			//1. 查询促销列表
			List<PromotionBO> promotionBOList = this.queryPromotionsByIds(promotionIdList);
			for(PromotionBO promotionBO : promotionBOList){
				PromotionBriefBOInfo promotionBriefBOInfo = new PromotionBriefBOInfo();
				promotionBriefBOInfo.setPromotionId(promotionBO.getId());
				promotionBriefBOInfo.setStatus(promotionMapStatus.get(promotionBO.getId()));
				
				//2. 查询商品信息
				SkuBO skuBO = iSkuService.querySkuById(promotionBO.getSkuId());
				promotionBriefBOInfo.setAdvert(skuBO.getAdverst());
				promotionBriefBOInfo.setSkuName(skuBO.getName());
				promotionBriefBOInfo.setSkuImgUrl(skuBO.getImgUrl());
				
				//3. 查询阶梯促销信息
				//3.1 根据促销id查询阶梯规则列表
				List<LadderPromotionBO> ladderPromotionBOList = iLadderPromotionService.queryLadderPromotionByPromotionId(promotionBO.getId());
				List<Long> ladderIdList =  new ArrayList<Long>();
				for(LadderPromotionBO ladderPromotionBO : ladderPromotionBOList){
					ladderIdList.add(ladderPromotionBO.getLadderId());
				}
				
				//3.2 查询阶梯规则中的最大折扣力度
				List<LadderBO> ladderBOList = iLadderService.queryLadderByIds(ladderIdList);
				BigDecimal maxDiscount= new BigDecimal("-1");
				for(LadderBO ladderBO : ladderBOList){
					if(maxDiscount.compareTo(ladderBO.getPriceDiscount()) >= 0){
						continue;
					}
					maxDiscount = ladderBO.getPriceDiscount();
				}
				promotionBriefBOInfo.setTopestDisconuntPercent(maxDiscount);
				promotionBriefBOInfo.setCheapestPromotionPrice(maxDiscount.divide(new BigDecimal("100")).multiply(skuBO.getOutprice()));
				promotionBriefInfoBOList.add(promotionBriefBOInfo);
			}
		}catch(Exception e){
			logger.error("PromotionServiceImpl.getPromotionBriefInfos查询促销简要信息时发生异常，入参{num="+JsonUtil.toJson(num)+" ,  promotionIdList="+JsonUtil.toJson(promotionIdList)+" }", e);
		}
		return promotionBriefInfoBOList;
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

	public void setiPromotionSequenceService(
			IPromotionSequenceService iPromotionSequenceService) {
		this.iPromotionSequenceService = iPromotionSequenceService;
	}

	public void setiSkuService(ISkuService iSkuService) {
		this.iSkuService = iSkuService;
	}

	public void setiLadderPromotionService(
			ILadderPromotionService iLadderPromotionService) {
		this.iLadderPromotionService = iLadderPromotionService;
	}

	public void setiLadderService(ILadderService iLadderService) {
		this.iLadderService = iLadderService;
	}

}
