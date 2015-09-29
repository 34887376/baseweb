package com.ms.domain.convert;

import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.dao.PromotionDAO;

public class PromotionConvert {

	public static PromotionBO convertDAOToBO(PromotionDAO promotionDAO){
		PromotionBO promotionBO= new PromotionBO();
		if(promotionDAO==null){
			return promotionBO;
		}
		promotionBO.setId(promotionDAO.getId());
		promotionBO.setSkuId(promotionDAO.getSkuId());
		promotionBO.setSkuNum(promotionDAO.getSkuNum());
		promotionBO.setYn(promotionDAO.isYn());
		return promotionBO;
	}
	
	public static PromotionDAO convertBOToDAO(PromotionBO promotionBO){
		PromotionDAO  promotionDAO =new PromotionDAO();
		if(promotionBO==null){
			return promotionDAO;
		}
		promotionDAO.setId(promotionBO.getId());
		promotionDAO.setSkuId(promotionBO.getSkuId());
		promotionDAO.setSkuNum(promotionBO.getSkuNum());
		promotionDAO.setYn(promotionBO.isYn());
		return promotionDAO;
	}
	
//	public static 
}
