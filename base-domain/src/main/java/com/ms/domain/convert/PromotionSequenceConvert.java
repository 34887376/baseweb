package com.ms.domain.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;
import com.ms.domain.promotionsequence.dao.PromotionSequenceDAO;

public class PromotionSequenceConvert {

	/**
	 * DAO转为BO
	 * @param promotionSequenceDAO
	 * @return
	 */
	public static PromotionSequenceBO convertDAOTOBO(PromotionSequenceDAO promotionSequenceDAO){
		PromotionSequenceBO promotionSequenceBO= new PromotionSequenceBO();
		if(promotionSequenceDAO==null){
			return promotionSequenceBO;
		}
		promotionSequenceBO.setEndTime(promotionSequenceDAO.getEndTime());
		promotionSequenceBO.setHasLoad(promotionSequenceDAO.getHasLoad());
		promotionSequenceBO.setId(promotionSequenceDAO.getId());
		promotionSequenceBO.setPreviosOrder(promotionSequenceDAO.getPreviosOrder());
		promotionSequenceBO.setYn(promotionSequenceDAO.getYn());
		promotionSequenceBO.setNextOrder(promotionSequenceDAO.getNextOrder());
		promotionSequenceBO.setPromotionId(promotionSequenceDAO.getPromotionId());
		promotionSequenceBO.setStartTime(promotionSequenceDAO.getStartTime());
		return promotionSequenceBO;
	}
	
	/**
	 * BO转为DAO
	 * @param promotionSequenceBO
	 * @return
	 */
	public static PromotionSequenceDAO convertBOTODAO(PromotionSequenceBO promotionSequenceBO){
		PromotionSequenceDAO promotionSequenceDAO = new PromotionSequenceDAO();
		if(promotionSequenceBO==null){
			return promotionSequenceDAO;
		}
		promotionSequenceDAO.setEndTime(promotionSequenceBO.getEndTime());
		promotionSequenceDAO.setHasLoad(promotionSequenceBO.getHasLoad());
		promotionSequenceDAO.setId(promotionSequenceBO.getId());
		promotionSequenceDAO.setPreviosOrder(promotionSequenceBO.getPreviosOrder());
		promotionSequenceDAO.setYn(promotionSequenceBO.getYn());
		promotionSequenceDAO.setNextOrder(promotionSequenceBO.getNextOrder());
		promotionSequenceDAO.setPromotionId(promotionSequenceBO.getPromotionId());
		promotionSequenceDAO.setStartTime(promotionSequenceBO.getStartTime());
		return promotionSequenceDAO;
	}
	
	/**
	 * DAOlist转化为BOList
	 * @param promotionSeqDAOList
	 * @return
	 */
	public static List<PromotionSequenceBO> convertDAOTOBOList(List<PromotionSequenceDAO> promotionSeqDAOList){
		List<PromotionSequenceBO> promotionSequenceBOList = new ArrayList<PromotionSequenceBO>();
		if(CollectionUtils.isEmpty(promotionSeqDAOList)){
			return promotionSequenceBOList;
		}
		for(PromotionSequenceDAO promotionSequenceDAO : promotionSeqDAOList){
			PromotionSequenceBO promotionSequenceBO= new PromotionSequenceBO();
			promotionSequenceBO.setEndTime(promotionSequenceDAO.getEndTime());
			promotionSequenceBO.setHasLoad(promotionSequenceDAO.getHasLoad());
			promotionSequenceBO.setId(promotionSequenceDAO.getId());
			promotionSequenceBO.setPreviosOrder(promotionSequenceDAO.getPreviosOrder());
			promotionSequenceBO.setYn(promotionSequenceDAO.getYn());
			promotionSequenceBO.setNextOrder(promotionSequenceDAO.getNextOrder());
			promotionSequenceBO.setPromotionId(promotionSequenceDAO.getPromotionId());
			promotionSequenceBO.setStartTime(promotionSequenceDAO.getStartTime());
			promotionSequenceBOList.add(promotionSequenceBO);
		}
		return promotionSequenceBOList;
		
	}
}
