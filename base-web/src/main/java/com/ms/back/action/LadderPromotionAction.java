package com.ms.back.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ms.action.constant.PageNumConstant;
import com.ms.domain.action.backstage.BackLadderPromotionResult;
import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;
import com.ms.service.backStage.ladder.face.IBackLadderPromotionService;

import base.test.base.action.BaseAction;
import base.test.base.util.JsonUtil;

public class LadderPromotionAction extends BaseAction{

	private static final long serialVersionUID = -3657474937768458735L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private IBackLadderPromotionService iBackLadderPromotionService;
	
	/**
	 * 主键id
	 */
	private Long ladderPromotionId;
	
	/**
	 * 促销id
	 */
	private Long promotionId;
	
	/**
	 * 阶梯促销规则id
	 */
	private Long ladderId;
	
	/**
	 * 是否有效
	 */
	private Integer isvaliade;
	
	public String queryLadderPromotionByPageNum(){
		try{
			List<LadderPromotionBO> ladderPromotionList = iBackLadderPromotionService.queryLadderPromotionByPageNum(PageNumConstant.PAGE, PageNumConstant.PAGE_SIZE);
	    	Map<String, Object> parmKeyValue = new HashMap<String, Object>();
	    	parmKeyValue.put("ladderPromotionList",ladderPromotionList);
	    	putParamToVm(parmKeyValue);
		}catch(Exception e){
			logger.error("LadderPromotionAction.queryLadderPromotionByPageNum查询阶梯促销信息异常！！！", e);
		}
		return "queryLadderPromotionByPageNum";
	}
	
	public String queryLadderPromotionByCondition(){
		BackLadderPromotionResult backLadderPromotionResult = new BackLadderPromotionResult();
		try{
			LadderPromotionBO ladderPromotionBO = new LadderPromotionBO();
			ladderPromotionBO.setLadderId(ladderId);
			ladderPromotionBO.setLadderPromotionId(ladderPromotionId);
			ladderPromotionBO.setPromotionId(promotionId);
			ladderPromotionBO.setYn(isvaliade==1?true:false);
			List<LadderPromotionBO> ladderPromotionList = iBackLadderPromotionService.queryLadderPromotionByCondition(ladderPromotionBO);
			backLadderPromotionResult.setMsg("查询成功！！！");
			backLadderPromotionResult.setSuccess(true);
			backLadderPromotionResult.setLadderPromotionBOList(ladderPromotionList);
			print(JsonUtil.toJson(backLadderPromotionResult));
		}catch(Exception e){
			logger.error("LadderPromotionAction.queryLadderPromotionByCondition查询阶梯信息异常！！！", e);
			backLadderPromotionResult.setMsg("查询异常！！！");
			backLadderPromotionResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderPromotionResult));
		}
		return "queryLadderPromotionByCondition";
	}
	
	public String addLadderPromotion(){
		BackLadderPromotionResult backLadderPromotionResult = new BackLadderPromotionResult();
		try{
			LadderPromotionBO ladderPromotionBO = new LadderPromotionBO();
			ladderPromotionBO.setLadderId(ladderId);
			ladderPromotionBO.setPromotionId(promotionId);
			ladderPromotionBO.setYn(isvaliade==1?true:false);
			boolean addResult = iBackLadderPromotionService.addLadderPromotion(ladderPromotionBO);
			if(addResult){
				backLadderPromotionResult.setMsg("添加成功！！！");
				backLadderPromotionResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}else{
				backLadderPromotionResult.setMsg("添加失败！！！");
				backLadderPromotionResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}
		}catch(Exception e){
			logger.error("LadderPromotionAction.addLadderPromotion添加阶梯信息异常！！！", e);
			backLadderPromotionResult.setMsg("查询异常！！！");
			backLadderPromotionResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderPromotionResult));
		}
		return "addLadderPromotion";
	}
	
	public String updateLadderPromotion(){
		BackLadderPromotionResult backLadderPromotionResult = new BackLadderPromotionResult();
		try{
			LadderPromotionBO ladderPromotionBO = new LadderPromotionBO();
			ladderPromotionBO.setLadderId(ladderId);
			ladderPromotionBO.setLadderPromotionId(ladderPromotionId);
			ladderPromotionBO.setPromotionId(promotionId);
			ladderPromotionBO.setYn(isvaliade==1?true:false);
			boolean updateResult = iBackLadderPromotionService.updateLadderPromotion(ladderPromotionBO);
			if(updateResult){
				backLadderPromotionResult.setMsg("更新成功！！！");
				backLadderPromotionResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}else{
				backLadderPromotionResult.setMsg("更新失败！！！");
				backLadderPromotionResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}
		}catch(Exception e){
			logger.error("LadderPromotionAction.updateLadderPromotion更新阶梯信息异常！！！", e);
			backLadderPromotionResult.setMsg("更新异常！！！");
			backLadderPromotionResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderPromotionResult));
		}
		return "updateLadderPromotion";
	}

	public String makeInvalidLadderPromotion(){
		BackLadderPromotionResult backLadderPromotionResult = new BackLadderPromotionResult();
		try{
			LadderPromotionBO ladderPromotionBO = new LadderPromotionBO();
			ladderPromotionBO.setLadderId(ladderId);
			ladderPromotionBO.setLadderPromotionId(ladderPromotionId);
			ladderPromotionBO.setPromotionId(promotionId);
			ladderPromotionBO.setYn(false);
			boolean updateResult = iBackLadderPromotionService.updateLadderPromotion(ladderPromotionBO);
			if(updateResult){
				backLadderPromotionResult.setMsg("置为无效成功！！！");
				backLadderPromotionResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}else{
				backLadderPromotionResult.setMsg("置为无效失败！！！");
				backLadderPromotionResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}
		}catch(Exception e){
			logger.error("LadderPromotionAction.delLadderPromotion置为无效阶梯信息异常！！！", e);
			backLadderPromotionResult.setMsg("置为无效异常！！！");
			backLadderPromotionResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderPromotionResult));
		}
		return "delLadderPromotion";
	}
	
	/**
	 * 物理删除
	 * @return
	 */
	public String physicalDel(){
		BackLadderPromotionResult backLadderPromotionResult = new BackLadderPromotionResult();
		try{
			List<Long> idList = new ArrayList<Long>();
			idList.add(ladderPromotionId);
			boolean delResult = iBackLadderPromotionService.delLadderPromotion(idList);
			if(delResult){
				backLadderPromotionResult.setMsg("删除成功！！！");
				backLadderPromotionResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}else{
				backLadderPromotionResult.setMsg("删除失败！！！");
				backLadderPromotionResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderPromotionResult));
			}
		}catch(Exception e){
			logger.error("LadderPromotionAction.physicalDel删除阶梯信息异常！！！", e);
			backLadderPromotionResult.setMsg("删除异常！！！");
			backLadderPromotionResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderPromotionResult));
		}
		return "physicalDel";
	}

	public Long getLadderPromotionId() {
		return ladderPromotionId;
	}

	public void setLadderPromotionId(Long ladderPromotionId) {
		this.ladderPromotionId = ladderPromotionId;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Long getLadderId() {
		return ladderId;
	}

	public void setLadderId(Long ladderId) {
		this.ladderId = ladderId;
	}

	public void setiBackLadderPromotionService(
			IBackLadderPromotionService iBackLadderPromotionService) {
		this.iBackLadderPromotionService = iBackLadderPromotionService;
	}

	public Integer getIsvaliade() {
		return isvaliade;
	}

	public void setIsvaliade(Integer isvaliade) {
		this.isvaliade = isvaliade;
	}
}
