package com.ms.dao.ladderpromotion.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ms.dao.base.dao.BaseMysqlDAO;
import com.ms.dao.ladderpromotion.face.ILadderPromotion;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;
import com.ms.domain.sku.dao.SkuDAO;

public class LadderPromotionDAOImpl extends BaseMysqlDAO implements ILadderPromotion {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String namespace="ladderpromotionTable.";
	
	public long addLadderPromotion(LadderPromotionDAO ladderPromotionDAO)  throws Exception{
		if(ladderPromotionDAO==null || ladderPromotionDAO.getLadderId()==null){
			return -1L;
		}
		Object effectObj = (Object)this.insert(namespace+"addLadderPromotion", ladderPromotionDAO);
		if(effectObj==null){
			return -1L;
		}
		if((Long)effectObj<=0){
			return -1L;
		}
		return ((Long)effectObj).longValue();
	}

	public boolean updateLadderPromotion(LadderPromotionDAO ladderPromotionDAO)  throws Exception{
		if(ladderPromotionDAO==null){
			return false;
		}
		int effectRow = this.update(namespace+"updateLadderPromotion", ladderPromotionDAO);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public boolean delLadderPromotion(LadderPromotionDAO ladderPromotionDAO)  throws Exception{
		if(ladderPromotionDAO==null){
			return false;
		}
		int effectRow = this.delete(namespace+"delLadderPromotion", ladderPromotionDAO);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public List<LadderPromotionDAO> queryLadderPromoitonByCondition(
			LadderPromotionDAO ladderPromotionDAO)  throws Exception{
		if(ladderPromotionDAO==null){
			return new ArrayList<LadderPromotionDAO>();
		}
		List<LadderPromotionDAO> ladderPromotionList = this.queryForList(namespace+"queryLadderPromoitonByCondition", ladderPromotionDAO);
		return ladderPromotionList;
	}

}
