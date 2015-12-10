package com.ms.service.backStage.ladder.face.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;

import com.ms.dao.ladderpromotion.face.ILadderPromotionDAO;
import com.ms.domain.convert.LadderPromotionConvert;
import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;
import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;
import com.ms.service.backStage.ladder.face.IBackLadderPromotionService;

public class BackLadderPromotionServiceImpl implements
		IBackLadderPromotionService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private ILadderPromotionDAO iLadderPromotionDAO;

	public List<LadderPromotionBO> queryLadderPromotionByPageNum(int page,
			int pageSize) {
		List<LadderPromotionBO> ladderPromotionBOList = new ArrayList<LadderPromotionBO>();
		try{
			List<LadderPromotionDAO> ladderPromotionDAOList = iLadderPromotionDAO.querySkuListByPageNum(page, pageSize);
			ladderPromotionBOList = LadderPromotionConvert.convertDAOTOBOList(ladderPromotionDAOList);
		}catch(Exception e){
			logger.error("BackLadderPromotionServiceImpl.queryLadderPromotionByPageNum查询阶梯促销信息时发生异常！！！", e);
		}
		return ladderPromotionBOList;
	}

	public List<LadderPromotionBO> queryLadderPromotionByCondition(
			LadderPromotionBO ladderPromotionBO) {
		List<LadderPromotionBO> ladderPromotionBOList = new ArrayList<LadderPromotionBO>();
		try{
			LadderPromotionDAO ladderPromotionDAO = LadderPromotionConvert.convertBOTODAO(ladderPromotionBO);
			List<LadderPromotionDAO> ladderPromotionDAOList = iLadderPromotionDAO.queryLadderPromoitonByCondition(ladderPromotionDAO );
			ladderPromotionBOList = LadderPromotionConvert.convertDAOTOBOList(ladderPromotionDAOList);
		}catch(Exception e){
			logger.error("BackLadderPromotionServiceImpl.queryLadderPromotionByCondition查询阶梯促销信息时发生异常！！！入参ladderPromotionBO={"+JsonUtil.toJson(ladderPromotionBO)+"}", e);
		}
		return ladderPromotionBOList;
	}

	public boolean addLadderPromotion(LadderPromotionBO ladderPromotionBO) {
		try{
			LadderPromotionDAO ladderPromotionDAO = LadderPromotionConvert.convertBOTODAO(ladderPromotionBO);
			long id= iLadderPromotionDAO.addLadderPromotion(ladderPromotionDAO);
			if(id>0){
				return true;
			}
		}catch(Exception e){
			logger.error("BackLadderPromotionServiceImpl.addLadderPromotion添加阶梯促销信息时发生异常！！！入参ladderPromotionBO={"+JsonUtil.toJson(ladderPromotionBO)+"}", e);
		}
		return false;
	}

	public boolean updateLadderPromotion(LadderPromotionBO ladderPromotionBO) {
		try{
			LadderPromotionDAO ladderPromotionDAO = LadderPromotionConvert.convertBOTODAO(ladderPromotionBO);
			boolean updateResult= iLadderPromotionDAO.updateLadderPromotion(ladderPromotionDAO);
			if(updateResult){
				return true;
			}
		}catch(Exception e){
			logger.error("BackLadderPromotionServiceImpl.updateLadderPromotion更新阶梯促销信息时发生异常！！！入参ladderPromotionBO={"+JsonUtil.toJson(ladderPromotionBO)+"}", e);
		}
		return false;
	}

	public boolean delLadderPromotion(List<Long> idList) {
		try{
			boolean updateResult= iLadderPromotionDAO.delLadderPromotion(idList);
			if(updateResult){
				return true;
			}
		}catch(Exception e){
			logger.error("BackLadderPromotionServiceImpl.updateLadderPromotion删除阶梯促销信息时发生异常！！！入参idList={"+JsonUtil.toJson(idList)+"}", e);
		}
		return false;
	}

	public void setiLadderPromotionDAO(ILadderPromotionDAO iLadderPromotionDAO) {
		this.iLadderPromotionDAO = iLadderPromotionDAO;
	}

}
