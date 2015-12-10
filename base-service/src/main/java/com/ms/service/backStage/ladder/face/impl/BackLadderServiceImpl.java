package com.ms.service.backStage.ladder.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ms.dao.ladder.face.ILadderDAO;
import com.ms.domain.convert.LadderConvert;
import com.ms.domain.ladder.bo.LadderBO;
import com.ms.domain.ladder.dao.LadderDAO;
import com.ms.service.backStage.ladder.face.IBackLadderService;

public class BackLadderServiceImpl implements IBackLadderService {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private ILadderDAO iLadderDAO;
	
	public List<LadderBO> queryLadderByPageNum(int page, int pageSize) {
		List<LadderBO> ladderBOList = new ArrayList<LadderBO>();
		try{
			List<LadderDAO> ladderList = iLadderDAO.queryLadderListByPageNum(page, pageSize);
			ladderBOList = LadderConvert.convertDAOTOBOList(ladderList);
		}catch(Exception e){
			logger.error("BackLadderServiceImpl.queryLadderByPageNum查询阶梯规则信息时发生异常！！！", e);
		}
		return ladderBOList;
	}

	public List<LadderBO> queryLadderByCondition(LadderBO ladderBO) {
		List<LadderBO> ladderBOList = new ArrayList<LadderBO>();
		try{
			LadderDAO ladderDAO = LadderConvert.convertBOTODAO(ladderBO);
			List<LadderDAO> ladderList = iLadderDAO.queryLadderListByCondition(ladderDAO );
			ladderBOList = LadderConvert.convertDAOTOBOList(ladderList);
		}catch(Exception e){
			logger.error("BackLadderServiceImpl.queryLadderByCondition查询阶梯规则信息时发生异常！！！", e);
		}
		return ladderBOList;
	}

	public boolean delLadder(List<Long> idList) {
		try{
			if(CollectionUtils.isEmpty(idList)){
				return false;
			}
			boolean isOk=true;
			for(Long id: idList){
				if(iLadderDAO.delLadder(id)==false){
					isOk=false;
				}
			}
			return isOk;
		}catch(Exception e){
			logger.error("BackLadderServiceImpl.delLadder删除阶梯规则信息时发生异常！！！", e);
		}
		return false;
	}

	public boolean updateLadder(LadderBO ladderBO) {
		try{
			LadderDAO ladderDAO = LadderConvert.convertBOTODAO(ladderBO);
			return iLadderDAO.updateLadder(ladderDAO);
		}catch(Exception e){
			logger.error("BackLadderServiceImpl.updateLadder更新阶梯规则信息时发生异常！！！", e);
		}
		return false;
	}

	public boolean addLadder(LadderBO ladderBO) {
		try{
			LadderDAO ladderDAO = LadderConvert.convertBOTODAO(ladderBO);
			long id= iLadderDAO.addLadder(ladderDAO);
			if(id>0){
				return true;
			}
		}catch(Exception e){
			logger.error("BackLadderServiceImpl.addLadder添加阶梯规则信息时发生异常！！！", e);
		}
		return false;
	}

	public void setiLadderDAO(ILadderDAO iLadderDAO) {
		this.iLadderDAO = iLadderDAO;
	}

}
