package com.ms.domain.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ms.domain.sku.bo.SkuBO;
import com.ms.domain.sku.dao.SkuDAO;

public class SkuConvert {

	/**
	 * DAO转BO
	 * @param ladderDAO
	 * @return
	 */
	public static SkuBO convertDAOTOBO(SkuDAO skuDAO){
		SkuBO skuBO = new SkuBO();
		if(skuDAO==null){
			return skuBO;
		}
		skuBO.setName(skuDAO.getName());
		skuBO.setNum(skuDAO.getNum());
		skuBO.setAdverst(skuDAO.getAdverst());
		skuBO.setImgUrl(skuDAO.getImgUrl());
		skuBO.setInprice(skuDAO.getInprice());
		skuBO.setOutprice(skuDAO.getOutprice());
		skuBO.setId(skuDAO.getId());
		skuBO.setYn(skuDAO.isYn());
		return skuBO;
	}
	
	/**
	 * BO转DAO
	 * @param ladderBO
	 * @return
	 */
	public static SkuDAO convertBOTODAO(SkuBO skuBO){
		SkuDAO skuDAO = new SkuDAO();
		if(skuBO==null){
			return skuDAO;
		}
		skuDAO.setId(skuBO.getId());
		skuDAO.setName(skuBO.getName());
		skuDAO.setNum(skuBO.getNum());
		skuDAO.setAdverst(skuBO.getAdverst());
		skuDAO.setImgUrl(skuBO.getImgUrl());
		skuDAO.setInprice(skuBO.getInprice());
		skuDAO.setOutprice(skuBO.getOutprice());
		skuDAO.setYn(skuBO.isYn());
		return skuDAO;
	}
	
	/**
	 * DAOList转BOList
	 * @param ladderDAO
	 * @return
	 */
	public static List<SkuBO> convertDAOTOBOList(List<SkuDAO> skuDAOList){
		List<SkuBO> skuBOList = new ArrayList<SkuBO>();
		if(CollectionUtils.isEmpty(skuDAOList)){
			return skuBOList;
		}
		for(SkuDAO skuDAO : skuDAOList){
			SkuBO skuBO = new SkuBO();
			skuBO.setName(skuDAO.getName());
			skuBO.setNum(skuDAO.getNum());
			skuBO.setAdverst(skuDAO.getAdverst());
			skuBO.setImgUrl(skuDAO.getImgUrl());
			skuBO.setInprice(skuDAO.getInprice());
			skuBO.setOutprice(skuDAO.getOutprice());
			skuBO.setId(skuDAO.getId());
			skuBO.setYn(skuDAO.isYn());
			skuBOList.add(skuBO);
		}
		return skuBOList;
	}
	
}
