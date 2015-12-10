package com.ms.service.backStage.promotion.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.dao.promotion.face.IPromotionDAO;
import com.ms.domain.convert.PromotionConvert;
import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.dao.PromotionDAO;
import com.ms.service.backStage.promotion.face.IBackPromotionService;

public class BackPromotionServiceImpl implements
		IBackPromotionService {

	private IPromotionDAO iPromotionDAO;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public List<PromotionBO> queryPromotionByPageNum(int page, int pageSize) {
		try {
			List<PromotionDAO> promotionDAOList = iPromotionDAO.queryPromotionByPageNum(page, pageSize);
			return PromotionConvert.convertDAOTOBOList(promotionDAOList);
		} catch (Exception e) {
			logger.error("BackPromotionManagerServiceImpl.queryPromotionByPageNum查询促销信息时发生异常！！！", e);
		}
		return new ArrayList<PromotionBO>();
	}

	public List<PromotionBO> queryPromotionByCondition(PromotionBO promotionBO) {
		try{
			PromotionDAO promotionDAO = PromotionConvert.convertBOTODAO(promotionBO);
			List<PromotionDAO> promotionDAOList = iPromotionDAO.queryPromotionByCondition(promotionDAO);
			return PromotionConvert.convertDAOTOBOList(promotionDAOList);
		} catch (Exception e) {
			logger.error("BackPromotionManagerServiceImpl.queryPromotionByCondition查询促销信息时发生异常！！！入参promotionBO={"+JsonUtil.toJson(promotionBO)+"}", e);
		}
		return new ArrayList<PromotionBO>();
	}

	public boolean addPromotion(PromotionBO promotionBO) {
		try{
			PromotionDAO promotionDAO = PromotionConvert.convertBOTODAO(promotionBO);
			long addresult = iPromotionDAO.addPromotion(promotionDAO);
			if(addresult>0){
				return true;
			}
		} catch (Exception e) {
			logger.error("BackPromotionManagerServiceImpl.addPromotion添加促销信息时发生异常！！！入参promotionBO={"+JsonUtil.toJson(promotionBO)+"}", e);
		}
		return false;
	}

	public boolean updatePromotion(PromotionBO promotionBO) {
		try{
			PromotionDAO promotionDAO = PromotionConvert.convertBOTODAO(promotionBO);
			boolean updateResult = iPromotionDAO.updatePromotion(promotionDAO);
			return updateResult;
		} catch (Exception e) {
			logger.error("BackPromotionManagerServiceImpl.updatePromotion更新促销信息时发生异常！！！入参promotionBO={"+JsonUtil.toJson(promotionBO)+"}", e);
		}
		return false;
	}

	public boolean delPromotion(List<Long> idList) {
		try{
			return iPromotionDAO.delPromotionsByIds(idList);
		} catch (Exception e) {
			logger.error("BackPromotionManagerServiceImpl.delPromotion删除促销信息时发生异常！！！入参idList={"+JsonUtil.toJson(idList)+"}", e);
		}
		return false;
	}

	public void setiPromotionDAO(IPromotionDAO iPromotionDAO) {
		this.iPromotionDAO = iPromotionDAO;
	}

}
