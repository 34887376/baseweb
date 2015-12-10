package com.ms.dao.sku.face.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ms.dao.base.dao.BaseMysqlDAO;
import com.ms.dao.sku.face.ISkuDAO;
import com.ms.domain.ladder.dao.LadderDAO;
import com.ms.domain.promotion.dao.PromotionDAO;
import com.ms.domain.sku.dao.SkuDAO;

public class SkuDAOImpl extends BaseMysqlDAO implements ISkuDAO {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String namespace="skuTable.";
	
	public long addSku(SkuDAO skuDAO) throws Exception {
		if(skuDAO==null || skuDAO.getName()==null){
			return -1L;
		}
		Object effectObj = (Object)this.insert(namespace+"addSku", skuDAO);
		if(effectObj==null){
			return -1L;
		}
		if((Long)effectObj<=0){
			return -1L;
		}
		return ((Long)effectObj).longValue();
	}

	public boolean updateSkuInfo(SkuDAO skuDAO) throws Exception {
		if(skuDAO==null){
			return false;
		}
		int effectRow = this.update(namespace+"updateSkuInfo", skuDAO);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public boolean delSkuInfo(Long id) throws Exception {
		if(id==null){
			return false;
		}
		int effectRow = this.delete(namespace+"delSkuInfo", id);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public SkuDAO querySkuById(Long skuId) throws Exception {
		if(skuId==null || skuId<0){
			return new SkuDAO();
		}
		SkuDAO skuDAO = (SkuDAO)this.queryForObject(namespace+"querySkuById", skuId);
		return skuDAO;
	}

	public List<SkuDAO> querySkuListByIds(List<Long> idList) throws Exception {
		if(CollectionUtils.isEmpty(idList)){
			return new ArrayList<SkuDAO>();
		}
		List<SkuDAO> skuList = this.queryForList(namespace+"querySkuListByIds", idList);
		return skuList;
	}
	
	public List<SkuDAO> querySkuListByPageNum(int page,int pageSize) throws Exception {
		if(page< 0 || pageSize<0){
			return new ArrayList<SkuDAO>();
		}
		Map<String,Integer> paramMap = new HashMap<String,Integer>();
		paramMap.put("startIndex", (page-1)*pageSize);
		paramMap.put("endIndex", page*pageSize);
		List<SkuDAO> skuList = this.queryForList(namespace+"querySkuListByPageNum", paramMap);
		return skuList;
	}

	public List<SkuDAO> querySkuListByCondition(SkuDAO skuDAO) throws Exception {
		if(skuDAO==null){
			return new ArrayList<SkuDAO>();
		}
		List<SkuDAO> skuList = this.queryForList(namespace+"queryPromotionByCondition", skuDAO);
		return skuList;
	}

}
