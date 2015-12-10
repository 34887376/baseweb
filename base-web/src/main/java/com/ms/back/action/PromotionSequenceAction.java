package com.ms.back.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ms.action.constant.PageNumConstant;
import com.ms.domain.action.backstage.BackPromotionSequenceResult;
import com.ms.domain.ladder.bo.LadderBO;
import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;
import com.ms.service.backStage.promotion.face.IBackPromotionSeqSerivce;

import base.test.base.action.BaseAction;
import base.test.base.util.JsonUtil;

public class PromotionSequenceAction extends BaseAction{

	private static final long serialVersionUID = -7516607160614961809L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private IBackPromotionSeqSerivce iBackPromotionSeqSerivce;
	
	/**
	 * 主键id
	 */
	private Long promotionSequenceId;
	
	/**
	 * 促销id
	 */
	private Long promotionId;
	
	/**
	 * 是否被加载过
	 */
	private Integer hasLoad;
	
	/**
	 * 上一个序号，指的是id
	 */
	private Long previosOrder;
	
	/**
	 * 下一个序号，指的是id
	 */
	private Long nextOrder;
	
	/**
	 * 促销开始时间
	 */
	private Date startTime;
	
	/**
	 * 促销结束时间
	 */
	private Date endTime;
	
	/**
	 * 是否有效
	 */
	private Integer isvaliade;
	

	public String queryPromotionSequenceByPageNum(){
		try{
			List<PromotionSequenceBO> promotionSeqList = iBackPromotionSeqSerivce.queryPromotionSequenceByPageNum(PageNumConstant.PAGE, PageNumConstant.PAGE_SIZE);
	    	Map<String, Object> parmKeyValue = new HashMap<String, Object>();
	    	parmKeyValue.put("promotionSeqList",promotionSeqList);
	    	putParamToVm(parmKeyValue);
		}catch(Exception e){
			logger.error("PromotionSequenceAction.queryPromotionSequenceByPageNum查询促销排序信息异常！！！", e);
		}
		return "queryPromotionSequenceByPageNum";
	}
	
	public String queryPromotionSequenceByCondition(){
		BackPromotionSequenceResult backPromotionSequenceResult = new BackPromotionSequenceResult();
		try{
			PromotionSequenceBO promotionSequenceBO = new PromotionSequenceBO();
			promotionSequenceBO.setEndTime(endTime);
			promotionSequenceBO.setStartTime(startTime);
			promotionSequenceBO.setHasLoad(hasLoad);
			promotionSequenceBO.setId(promotionSequenceId);
			promotionSequenceBO.setNextOrder(nextOrder);
			promotionSequenceBO.setPreviosOrder(previosOrder);
			promotionSequenceBO.setPromotionId(promotionId);
			promotionSequenceBO.setYn(isvaliade);
			List<PromotionSequenceBO> promotionSequenceBOList = iBackPromotionSeqSerivce.queryPromotionSequenceByCondition(promotionSequenceBO);
			backPromotionSequenceResult.setSuccess(true);
			backPromotionSequenceResult.setMsg("查询成功！！！");
			backPromotionSequenceResult.setPromotionSequenceBOList(promotionSequenceBOList);
			print(JsonUtil.toJson(backPromotionSequenceResult));
		}catch(Exception e){
			logger.error("PromotionSequenceAction.queryPromotionSequenceByCondition查询促销排序信息异常！！！", e);
			backPromotionSequenceResult.setSuccess(false);
			backPromotionSequenceResult.setMsg("查询异常！！！");
			print(JsonUtil.toJson(backPromotionSequenceResult));
		}
		return "queryPromotionSequenceByCondition";
	}
	
	public String addPromotionSequence(){
		BackPromotionSequenceResult backPromotionSequenceResult = new BackPromotionSequenceResult();
		try{
			PromotionSequenceBO promotionSequenceBO = new PromotionSequenceBO();
			promotionSequenceBO.setEndTime(endTime);
			promotionSequenceBO.setStartTime(startTime);
			promotionSequenceBO.setHasLoad(hasLoad);
			promotionSequenceBO.setNextOrder(nextOrder);
			promotionSequenceBO.setPreviosOrder(previosOrder);
			promotionSequenceBO.setPromotionId(promotionId);
			promotionSequenceBO.setYn(isvaliade);
			boolean addResult = iBackPromotionSeqSerivce.addPromotionSequence(promotionSequenceBO);
			if(addResult){
				backPromotionSequenceResult.setSuccess(true);
				backPromotionSequenceResult.setMsg("添加成功！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}else{
				backPromotionSequenceResult.setSuccess(false);
				backPromotionSequenceResult.setMsg("添加失败！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}

		}catch(Exception e){
			logger.error("PromotionSequenceAction.addPromotionSequence添加促销排序信息异常！！！", e);
			backPromotionSequenceResult.setSuccess(false);
			backPromotionSequenceResult.setMsg("添加异常！！！");
			print(JsonUtil.toJson(backPromotionSequenceResult));
		}
		return "addPromotionSequence";
		
	}
	
	public String updatePromotionSequence(){
		BackPromotionSequenceResult backPromotionSequenceResult = new BackPromotionSequenceResult();
		try{
			PromotionSequenceBO promotionSequenceBO = new PromotionSequenceBO();
			promotionSequenceBO.setId(promotionSequenceId);
			promotionSequenceBO.setEndTime(endTime);
			promotionSequenceBO.setStartTime(startTime);
			promotionSequenceBO.setHasLoad(hasLoad);
			promotionSequenceBO.setNextOrder(nextOrder);
			promotionSequenceBO.setPreviosOrder(previosOrder);
			promotionSequenceBO.setPromotionId(promotionId);
			promotionSequenceBO.setYn(isvaliade);
			boolean updateResult = iBackPromotionSeqSerivce.updatePromotionSequence(promotionSequenceBO);
			if(updateResult){
				backPromotionSequenceResult.setSuccess(true);
				backPromotionSequenceResult.setMsg("更新成功！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}else{
				backPromotionSequenceResult.setSuccess(false);
				backPromotionSequenceResult.setMsg("更新失败！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}

		}catch(Exception e){
			logger.error("PromotionSequenceAction.updatePromotionSequence更新促销排序信息异常！！！", e);
			backPromotionSequenceResult.setSuccess(false);
			backPromotionSequenceResult.setMsg("更新异常！！！");
			print(JsonUtil.toJson(backPromotionSequenceResult));
		}
		return "updatePromotionSequence";
	}

	public String makeInvalidPromotionSequence(){
		BackPromotionSequenceResult backPromotionSequenceResult = new BackPromotionSequenceResult();
		try{
			PromotionSequenceBO promotionSequenceBO = new PromotionSequenceBO();
			promotionSequenceBO.setId(promotionSequenceId);
			promotionSequenceBO.setEndTime(endTime);
			promotionSequenceBO.setStartTime(startTime);
			promotionSequenceBO.setHasLoad(hasLoad);
			promotionSequenceBO.setNextOrder(nextOrder);
			promotionSequenceBO.setPreviosOrder(previosOrder);
			promotionSequenceBO.setPromotionId(promotionId);
			promotionSequenceBO.setYn(0);
			boolean updateResult = iBackPromotionSeqSerivce.updatePromotionSequence(promotionSequenceBO);
			if(updateResult){
				backPromotionSequenceResult.setSuccess(true);
				backPromotionSequenceResult.setMsg("设置无效成功！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}else{
				backPromotionSequenceResult.setSuccess(false);
				backPromotionSequenceResult.setMsg("设置无效失败！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}

		}catch(Exception e){
			logger.error("PromotionSequenceAction.delPromotionSequence设置无效促销排序信息异常！！！", e);
			backPromotionSequenceResult.setSuccess(false);
			backPromotionSequenceResult.setMsg("设置无效异常！！！");
			print(JsonUtil.toJson(backPromotionSequenceResult));
		}
		return "makeInvalidPromotionSequence";
	}
	
	/**
	 * 物理删除
	 * @return
	 */
	public String physicalDel(){
		BackPromotionSequenceResult backPromotionSequenceResult = new BackPromotionSequenceResult();
		try{
			List<Long> idList = new ArrayList<Long>();
			idList.add(promotionSequenceId);
			boolean delResult = iBackPromotionSeqSerivce.delPromotionSequence(idList);
			if(delResult){
				backPromotionSequenceResult.setSuccess(true);
				backPromotionSequenceResult.setMsg("删除！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}else{
				backPromotionSequenceResult.setSuccess(false);
				backPromotionSequenceResult.setMsg("删除失败！！！");
				print(JsonUtil.toJson(backPromotionSequenceResult));
			}

		}catch(Exception e){
			logger.error("PromotionSequenceAction.physicalDel物理删除促销排序信息异常！！！", e);
			backPromotionSequenceResult.setSuccess(false);
			backPromotionSequenceResult.setMsg("删除异常！！！");
			print(JsonUtil.toJson(backPromotionSequenceResult));
		}
		return "physicalDel";
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Integer getHasLoad() {
		return hasLoad;
	}

	public void setHasLoad(Integer hasLoad) {
		this.hasLoad = hasLoad;
	}

	public Long getPreviosOrder() {
		return previosOrder;
	}

	public void setPreviosOrder(Long previosOrder) {
		this.previosOrder = previosOrder;
	}

	public Long getNextOrder() {
		return nextOrder;
	}

	public void setNextOrder(Long nextOrder) {
		this.nextOrder = nextOrder;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setiBackPromotionSeqSerivce(
			IBackPromotionSeqSerivce iBackPromotionSeqSerivce) {
		this.iBackPromotionSeqSerivce = iBackPromotionSeqSerivce;
	}

	public Long getPromotionSequenceId() {
		return promotionSequenceId;
	}

	public void setPromotionSequenceId(Long promotionSequenceId) {
		this.promotionSequenceId = promotionSequenceId;
	}

	public Integer getIsvaliade() {
		return isvaliade;
	}

	public void setIsvaliade(Integer isvaliade) {
		this.isvaliade = isvaliade;
	}
}
