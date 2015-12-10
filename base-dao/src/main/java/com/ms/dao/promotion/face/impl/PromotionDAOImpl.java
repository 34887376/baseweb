package com.ms.dao.promotion.face.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ms.dao.base.dao.BaseMysqlDAO;
import com.ms.dao.promotion.face.IPromotionDAO;
import com.ms.domain.promotion.dao.PromotionDAO;
import com.ms.domain.sku.dao.SkuDAO;

public class PromotionDAOImpl extends BaseMysqlDAO implements IPromotionDAO {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String namespace="promotionTable.";

	public PromotionDAO queryPromotionById(Long id) throws Exception{
		if(id==null || id<0){
			return new PromotionDAO();
		}
		PromotionDAO promotionDAO = (PromotionDAO)this.queryForObject(namespace+"queryPromotionById", id);
		return promotionDAO;
	}

	public List<PromotionDAO> queryPromotionsByIds(List<Long> idList) throws Exception{
		if(CollectionUtils.isEmpty(idList)){
			return new ArrayList<PromotionDAO>();
		}
		List<PromotionDAO> promotionList = this.queryForList(namespace+"queryPromotionsByIds", idList);
		return promotionList;
	}

	public long addPromotion(PromotionDAO promotionDAO) throws Exception{
		if(promotionDAO==null || promotionDAO.getSkuId()==null){
			return -1L;
		}
		Object effectObj = (Object)this.insert(namespace+"addPromotion", promotionDAO);
		if(effectObj==null){
			return -1L;
		}
		if((Long)effectObj<=0){
			return -1L;
		}
		return ((Long)effectObj).longValue();
	}

	public boolean delPromotionsByIds(List<Long> idList) throws Exception{
		if(CollectionUtils.isEmpty(idList)){
			return false;
		}
		int effectRow = this.delete(namespace+"delPromotionsByIds", idList);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public boolean updatePromotion(PromotionDAO promotionDAO) throws Exception{
		if(promotionDAO==null){
			return false;
		}
		int effectRow = this.update(namespace+"updatePromotion", promotionDAO);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public List<PromotionDAO> queryPromotionByCondition(PromotionDAO promotionDAO)
			throws Exception {
		if(promotionDAO==null){
			return new ArrayList<PromotionDAO>();
		}
		List<PromotionDAO> promotionList = this.queryForList(namespace+"queryPromotionByCondition", promotionDAO);
		return promotionList;
	}

	public List<PromotionDAO> queryPromotionByPageNum(int page, int pageSize)
			throws Exception {
		if(page< 0 || pageSize<0){
			return new ArrayList<PromotionDAO>();
		}
		Map<String,Integer> paramMap = new HashMap<String,Integer>();
		paramMap.put("startIndex", (page-1)*pageSize);
		paramMap.put("endIndex", page*pageSize);
		List<PromotionDAO> promotionList = this.queryForList(namespace+"queryPromotionListByPageNum", paramMap);
		return promotionList;
	}

}
