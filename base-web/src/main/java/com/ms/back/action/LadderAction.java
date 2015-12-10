package com.ms.back.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ms.action.constant.PageNumConstant;
import com.ms.domain.action.backstage.BackLadderResult;
import com.ms.domain.ladder.bo.LadderBO;
import com.ms.service.backStage.ladder.face.IBackLadderService;

import base.test.base.action.BaseAction;
import base.test.base.util.JsonUtil;

public class LadderAction extends BaseAction{

	private static final long serialVersionUID = -2181997474569899500L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private IBackLadderService iBackLadderService;
	
	/**
	 * 阶梯促销规则id
	 */
	private Long ladderId;
	
	/**
	 * 价格折扣比率(商品原价的)
	 */
	private String priceDiscount;
	
	/**
	 * 商品数量占比(促销商品总数的百分比)
	 */
	private String numPercent;
	
	/**
	 * 阶梯类型
	 */
	private Integer type;
	
	/**
	 * 是否有效
	 */
	private Integer isvaliade;
	
	/**
	 * 分页查询阶梯规则信息
	 * @return
	 */
	public String queryLadderByPageNum(){
		try{
			List<LadderBO> ladderList = iBackLadderService.queryLadderByPageNum(PageNumConstant.PAGE, PageNumConstant.PAGE_SIZE);
	    	Map<String, Object> parmKeyValue = new HashMap<String, Object>();
	    	parmKeyValue.put("ladderList",ladderList);
	    	putParamToVm(parmKeyValue);
		}catch(Exception e){
			logger.error("LadderAction.queryLadderByPageNum查询阶梯信息异常！！！", e);
		}
		return "queryLadderByPageNum";
	}
	
	/**
	 * 根据条件查询阶梯规则
	 * @return
	 */
	public String queryLadderByCondition(){
		BackLadderResult backLadderResult = new BackLadderResult();
		try{
			LadderBO ladderBO = new LadderBO();
			ladderBO.setId(ladderId);
			if(numPercent!=null){
				ladderBO.setNumPercent(new BigDecimal(numPercent));
			}
			if(priceDiscount!=null){
				ladderBO.setPriceDiscount(new BigDecimal(priceDiscount));
			}
			ladderBO.setType(type);
			if(isvaliade!=null){
				ladderBO.setYn(isvaliade==1?true:false);
			}
			List<LadderBO> ladderList = iBackLadderService.queryLadderByCondition(ladderBO);
			backLadderResult.setMsg("查询成功！！！");
			backLadderResult.setSuccess(true);
			backLadderResult.setLadderList(ladderList);
			print(JsonUtil.toJson(backLadderResult));
		}catch(Exception e){
			logger.error("LadderAction.queryLadderByCondition查询阶梯信息异常！！！", e);
			backLadderResult.setMsg("查询异常！！！");
			backLadderResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderResult));
		}
		return "queryLadderByCondition";
	}
	
	/**
	 * 添加阶梯规则
	 * @return
	 */
	public String addLadder(){
		BackLadderResult backLadderResult = new BackLadderResult();
		try{
			LadderBO ladderBO = new LadderBO();
			ladderBO.setNumPercent(new BigDecimal(numPercent));
			ladderBO.setPriceDiscount(new BigDecimal(priceDiscount));
			ladderBO.setType(type);
			ladderBO.setYn(isvaliade==1?true:false);
			boolean addResult = iBackLadderService.addLadder(ladderBO);
			if(addResult){
				backLadderResult.setMsg("添加成功！！！");
				backLadderResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderResult));
			}else{
				backLadderResult.setMsg("添加失败！！！");
				backLadderResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderResult));
			}
		}catch(Exception e){
			logger.error("LadderAction.addLadder添加阶梯信息异常！！！", e);
			backLadderResult.setMsg("查询异常！！！");
			backLadderResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderResult));
		}
		return "addLadder";
	}
	
	/**
	 * 更新阶梯规则
	 * @return
	 */
	public String updateLadder(){
		BackLadderResult backLadderResult = new BackLadderResult();
		try{
			LadderBO ladderBO = new LadderBO();
			ladderBO.setId(ladderId);
			ladderBO.setNumPercent(new BigDecimal(numPercent));
			ladderBO.setPriceDiscount(new BigDecimal(priceDiscount));
			ladderBO.setType(type);
			ladderBO.setYn(isvaliade==1?true:false);
			boolean updateResult = iBackLadderService.updateLadder(ladderBO);
			if(updateResult){
				backLadderResult.setMsg("更新成功！！！");
				backLadderResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderResult));
			}else{
				backLadderResult.setMsg("更新失败！！！");
				backLadderResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderResult));
			}
		}catch(Exception e){
			logger.error("LadderAction.updateLadder更新阶梯信息异常！！！", e);
			backLadderResult.setMsg("更新异常！！！");
			backLadderResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderResult));
		}
		return "updateLadder";
	}
	
	
	/**
	 * 逻辑删除阶梯规则
	 * @return
	 */
	public String makeInvalidLadder(){
		BackLadderResult backLadderResult = new BackLadderResult();
		try{
			LadderBO ladderBO = new LadderBO();
			ladderBO.setId(ladderId);
//			ladderBO.setNumPercent(new BigDecimal(numPercent));
//			ladderBO.setPriceDiscount(new BigDecimal(priceDiscount));
//			ladderBO.setType(type);
			ladderBO.setYn(false);
			boolean updateResult = iBackLadderService.updateLadder(ladderBO);
			if(updateResult){
				backLadderResult.setMsg("置为无效成功！！！");
				backLadderResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderResult));
			}else{
				backLadderResult.setMsg("置为无效失败！！！");
				backLadderResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderResult));
			}
		}catch(Exception e){
			logger.error("LadderAction.delLadder置为无效阶梯信息异常！！！", e);
			backLadderResult.setMsg("置为无效异常！！！");
			backLadderResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderResult));
		}
		
		return "makeInvalidLadder";
	}

	/**
	 * 物理删除
	 * @return
	 */
	public String physicalDel(){
		BackLadderResult backLadderResult = new BackLadderResult();
		try{
			List<Long> idList = new ArrayList<Long>();
			idList.add(ladderId);
			boolean delResult = iBackLadderService.delLadder(idList);
			if(delResult){
				backLadderResult.setMsg("物理删除成功！！！");
				backLadderResult.setSuccess(true);
				print(JsonUtil.toJson(backLadderResult));
			}else{
				backLadderResult.setMsg("物理删除失败！！！");
				backLadderResult.setSuccess(false);
				print(JsonUtil.toJson(backLadderResult));
			}
		}catch(Exception e){
			logger.error("LadderAction.physicalDel物理删除阶梯信息异常！！！", e);
			backLadderResult.setMsg("物理删除异常！！！");
			backLadderResult.setSuccess(false);
			print(JsonUtil.toJson(backLadderResult));
		}
		return "physicalDel";
	}
	

	public String getPriceDiscount() {
		return priceDiscount;
	}

	public void setPriceDiscount(String priceDiscount) {
		this.priceDiscount = priceDiscount;
	}

	public String getNumPercent() {
		return numPercent;
	}

	public void setNumPercent(String numPercent) {
		this.numPercent = numPercent;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setiBackLadderService(IBackLadderService iBackLadderService) {
		this.iBackLadderService = iBackLadderService;
	}

	public Integer getIsvaliade() {
		return isvaliade;
	}

	public void setIsvaliade(Integer isvaliade) {
		this.isvaliade = isvaliade;
	}

	public Long getLadderId() {
		return ladderId;
	}

	public void setLadderId(Long ladderId) {
		this.ladderId = ladderId;
	}

}
