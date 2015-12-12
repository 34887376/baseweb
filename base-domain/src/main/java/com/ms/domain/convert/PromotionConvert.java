package com.ms.domain.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.dao.PromotionDAO;

public class PromotionConvert {

	/**
	 * BO转为DAO
	 * @param promotionBO
	 * @return
	 */
	public static PromotionDAO convertBOTODAO(PromotionBO promotionBO){
		PromotionDAO promotionDAO = new PromotionDAO();
		if(promotionBO == null){
			return promotionDAO;
		}
		promotionDAO.setId(promotionBO.getId());
		promotionDAO.setSkuId(promotionBO.getSkuId());
		promotionDAO.setSkuNum(promotionBO.getSkuNum());
		promotionDAO.setYn(promotionBO.getYn());
		return promotionDAO;
	}
	
	/**
	 * DAO转为BO
	 * @param promotionDAO
	 * @return
	 */
	public static PromotionBO convertDAOTOBO(PromotionDAO promotionDAO){
		PromotionBO promotionBO = new PromotionBO();
		if(promotionDAO == null){
			return promotionBO;
		}
		promotionBO.setId(promotionDAO.getId());
		promotionBO.setSkuId(promotionDAO.getSkuId());
		promotionBO.setSkuNum(promotionDAO.getSkuNum());
		promotionBO.setYn(promotionDAO.getYn());
		return promotionBO;
	}
	
	/**
	 * DAOList转为BOList
	 * @param promotionDAO
	 * @return
	 */
	public static List<PromotionBO> convertDAOTOBOList(List<PromotionDAO> promotionDAOList){
		List<PromotionBO> promotionBOList = new ArrayList<PromotionBO>();
		if(CollectionUtils.isEmpty(promotionDAOList)){
			return promotionBOList;
		}
		for(PromotionDAO promotionDAO : promotionDAOList){
			PromotionBO promotionBO = new PromotionBO();
			promotionBO.setId(promotionDAO.getId());
			promotionBO.setSkuId(promotionDAO.getSkuId());
			promotionBO.setSkuNum(promotionDAO.getSkuNum());
			promotionBO.setYn(promotionDAO.getYn());
			promotionBOList.add(promotionBO);
		}
		
		return promotionBOList;
	}
}
