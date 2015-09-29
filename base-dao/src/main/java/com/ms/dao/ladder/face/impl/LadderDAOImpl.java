package com.ms.dao.ladder.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ms.dao.base.dao.BaseMysqlDAO;
import com.ms.dao.ladder.face.ILadderDAO;
import com.ms.domain.ladder.dao.LadderDAO;
import com.ms.domain.promotion.dao.PromotionDAO;

public class LadderDAOImpl extends BaseMysqlDAO implements ILadderDAO {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String namespace="ladderTable.";
	
	public long addLadder(LadderDAO ladderDAO) throws Exception {
		if(ladderDAO==null || ladderDAO.getNumPercent()==null){
			return -1L;
		}
		Object effectObj = (Object)this.insert(namespace+"addLadder", ladderDAO);
		if(effectObj==null){
			return -1L;
		}
		if((Long)effectObj<=0){
			return -1L;
		}
		return ((Long)effectObj).longValue();
	}

	public boolean delLadder(Long id) throws Exception{
		if(id==null){
			return false;
		}
		int effectRow = this.delete(namespace+"delLadder", id);
		if(effectRow>0){
			return true;
		}
		return false;
	}

	public boolean updateLadder(LadderDAO ladderDAO) throws Exception{
		if(ladderDAO==null){
			return false;
		}
		int effectRow = this.update(namespace+"updateLadder", ladderDAO);
		if(effectRow>0){
			return true;
		}
		return false;
	}
	
	public LadderDAO queryLadderById(Long id) throws Exception{
		if(id==null || id<0){
			return new LadderDAO();
		}
		LadderDAO ladderDAO = (LadderDAO)this.queryForObject(namespace+"queryLadderById", id);
		return ladderDAO;
	}

	public List<LadderDAO> queryLadderList(List<Long> idList) throws Exception{
		if(CollectionUtils.isEmpty(idList)){
			return new ArrayList<LadderDAO>();
		}
		List<LadderDAO> ladderList = this.queryForList(namespace+"queryLadderList", idList);
		return ladderList;
	}

	public List<LadderDAO> queryAllLadder() throws Exception{
		List<LadderDAO> ladderList = this.queryForList(namespace+"queryAllLadder");
		return ladderList;
	}

}
