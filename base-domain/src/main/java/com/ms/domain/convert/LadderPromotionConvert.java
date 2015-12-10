package com.ms.domain.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;

public class LadderPromotionConvert {

	/**
	 * DAO转BO
	 * @param ladderDAO
	 * @return
	 */
	public static LadderPromotionBO convertDAOTOBO(LadderPromotionDAO ladderPromotionDAO){
		LadderPromotionBO ladderPromotionBO = new LadderPromotionBO();
		if(ladderPromotionDAO==null){
			return ladderPromotionBO;
		}
		ladderPromotionBO.setLadderId(ladderPromotionDAO.getLadderId());
		ladderPromotionBO.setLadderPromotionId(ladderPromotionDAO.getId());
		ladderPromotionBO.setPromotionId(ladderPromotionDAO.getPromotionId());
		ladderPromotionBO.setYn(ladderPromotionDAO.getYn());
		return ladderPromotionBO;
	}
	
	/**
	 * BO转DAO
	 * @param ladderBO
	 * @return
	 */
	public static LadderPromotionDAO convertBOTODAO(LadderPromotionBO ladderPromotionBO){
		LadderPromotionDAO ladderPromotionDAO = new LadderPromotionDAO();
		if(ladderPromotionBO==null){
			return ladderPromotionDAO;
		}
		ladderPromotionDAO.setLadderId(ladderPromotionBO.getLadderId());
		ladderPromotionDAO.setId(ladderPromotionBO.getLadderPromotionId());
		ladderPromotionDAO.setPromotionId(ladderPromotionBO.getPromotionId());
		ladderPromotionDAO.setYn(ladderPromotionBO.getYn());
		return ladderPromotionDAO;
	}
	
	/**
	 * DAOList转BOList
	 * @param ladderDAO
	 * @return
	 */
	public static List<LadderPromotionBO> convertDAOTOBOList(List<LadderPromotionDAO> ladderPromotionDAOList){
		List<LadderPromotionBO> ladderPromotionBOList = new ArrayList<LadderPromotionBO>();
		if(CollectionUtils.isEmpty(ladderPromotionDAOList)){
			return ladderPromotionBOList;
		}
		for(LadderPromotionDAO ladderPromotionDAO : ladderPromotionDAOList){
			LadderPromotionBO ladderPromotionBO = new LadderPromotionBO();
			ladderPromotionBO.setLadderId(ladderPromotionDAO.getLadderId());
			ladderPromotionBO.setLadderPromotionId(ladderPromotionDAO.getPromotionId());
			ladderPromotionBO.setPromotionId(ladderPromotionDAO.getPromotionId());
			ladderPromotionBO.setYn(ladderPromotionDAO.getYn());
			ladderPromotionBOList.add(ladderPromotionBO);
		}
		return ladderPromotionBOList;
	}
	
}
