package com.ms.domain.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ms.domain.ladder.bo.LadderBO;
import com.ms.domain.ladder.dao.LadderDAO;

public class LadderConvert {

	/**
	 * DAO转BO
	 * @param ladderDAO
	 * @return
	 */
	public static LadderBO convertDAOTOBO(LadderDAO ladderDAO){
		LadderBO ladderBO = new LadderBO();
		if(ladderDAO==null){
			return ladderBO;
		}
		ladderBO.setId(ladderDAO.getId());
		ladderBO.setNumPercent(ladderDAO.getNumPercent());
		ladderBO.setPriceDiscount(ladderDAO.getPriceDiscount());
		ladderBO.setType(ladderDAO.getType());
		ladderBO.setYn(ladderDAO.getYn());
		return ladderBO;
	}
	
	/**
	 * BO转DAO
	 * @param ladderBO
	 * @return
	 */
	public static LadderDAO convertBOTODAO(LadderBO ladderBO){
		LadderDAO ladderDAO = new LadderDAO();
		if(ladderBO==null){
			return ladderDAO;
		}
		ladderDAO.setId(ladderBO.getId());
		ladderDAO.setNumPercent(ladderBO.getNumPercent());
		ladderDAO.setPriceDiscount(ladderBO.getPriceDiscount());
		ladderDAO.setType(ladderBO.getType());
		ladderDAO.setYn(ladderBO.getYn());
		return ladderDAO;
	}
	
	/**
	 * DAOList转BOList
	 * @param ladderDAO
	 * @return
	 */
	public static List<LadderBO> convertDAOTOBOList(List<LadderDAO> ladderDAOList){
		List<LadderBO> ladderBOList = new ArrayList<LadderBO>();
		if(CollectionUtils.isEmpty(ladderDAOList)){
			return ladderBOList;
		}
		for(LadderDAO ladderDAO : ladderDAOList){
			LadderBO ladderBO = new LadderBO();
			ladderBO.setId(ladderDAO.getId());
			ladderBO.setNumPercent(ladderDAO.getNumPercent());
			ladderBO.setPriceDiscount(ladderDAO.getPriceDiscount());
			ladderBO.setType(ladderDAO.getType());
			ladderBO.setYn(ladderDAO.getYn());
			ladderBOList.add(ladderBO);
		}
		return ladderBOList;
	}
	
}
