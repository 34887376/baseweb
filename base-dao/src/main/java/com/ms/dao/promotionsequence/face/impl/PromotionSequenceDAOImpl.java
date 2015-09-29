package com.ms.dao.promotionsequence.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ms.dao.base.dao.BaseMysqlDAO;
import com.ms.dao.promotionsequence.face.IPromotionSequenceDAO;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;
import com.ms.domain.promotionsequence.dao.PromotionSequenceDAO;

public class PromotionSequenceDAOImpl extends BaseMysqlDAO implements IPromotionSequenceDAO {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String namespace="promotionSequenceTable.";
	
	public long addPromotionSequence(PromotionSequenceDAO promotionSequenceDAO)
			throws Exception {
		if(promotionSequenceDAO==null || promotionSequenceDAO.getPromotionId()==null){
			return -1L;
		}
		Object effectObj = (Object)this.insert(namespace+"addPromotionSequence", promotionSequenceDAO);
		if(effectObj==null){
			return -1L;
		}
		if((Long)effectObj<=0){
			return -1L;
		}
		return ((Long)effectObj).longValue();
	}

	public boolean updatePromotionSequence(
			PromotionSequenceDAO promotionSequenceDAO) throws Exception {
		if(promotionSequenceDAO==null){
			return false;
		}
		int effectRow = this.update(namespace+"updatePromotionSequence", promotionSequenceDAO);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public boolean delPromotionSequence(
			PromotionSequenceDAO promotionSequenceDAO) throws Exception {
		if(promotionSequenceDAO==null){
			return false;
		}
		int effectRow = this.delete(namespace+"delPromotionSequence", promotionSequenceDAO);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public List<PromotionSequenceDAO> queryPromotionSequence(
			PromotionSequenceDAO promotionSequenceDAO) throws Exception {
		if(promotionSequenceDAO==null){
			return new ArrayList<PromotionSequenceDAO>();
		}
		List<PromotionSequenceDAO> promotionSequenceList = this.queryForList(namespace+"queryPromotionSequence", promotionSequenceDAO);
		return promotionSequenceList;
	}

}
