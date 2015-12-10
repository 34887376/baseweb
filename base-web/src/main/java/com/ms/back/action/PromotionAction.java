package com.ms.back.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ms.domain.action.backstage.BackPromotionResult;
import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.service.backStage.promotion.face.IBackPromotionService;

import base.test.base.action.BaseAction;
import base.test.base.util.JsonUtil;

public class PromotionAction extends BaseAction{

	private static final long serialVersionUID = 4623901713432794859L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private IBackPromotionService iBackPromotionService;
	
	/**
	 * 促销id
	 */
	private Long promotionId;

	/**
	 * 商品id
	 */
	private Long skuId;
	
	/**
	 * 促销的商品数量
	 */
	private Integer skuNum;
	
	/**
	 * 是否有效
	 */
	private Integer yn;

	
	/**
	 * 添加促销信息
	 * @return
	 */
	public String addPromotion(){
		BackPromotionResult backPromotionResult = new BackPromotionResult();
		try{
			PromotionBO promotionBO = new PromotionBO();
			promotionBO.setSkuId(skuId);
			promotionBO.setSkuNum(skuNum);
			promotionBO.setYn(yn==1?true:false);
			boolean addResult = iBackPromotionService.addPromotion(promotionBO);
			
			if(addResult){
				backPromotionResult.setSuccess(true);
				backPromotionResult.setMsg("添加成功！！！");
				print(JsonUtil.toJson(backPromotionResult));
			}else{
				backPromotionResult.setSuccess(false);
				backPromotionResult.setMsg("添加失败！！！");
				print(JsonUtil.toJson(backPromotionResult));
			}
		}catch(Exception e){
			logger.error("PromotionAction.addPromotion添加促销信息异常！！！上下文参数={skuId="+skuId+",  skuNum="+skuNum+",  yn="+yn+"}", e);
			backPromotionResult.setSuccess(false);
			backPromotionResult.setMsg("添加异常！！！");
			print(JsonUtil.toJson(backPromotionResult));
		}
		return "addPromotion";
	}
	
	/**
	 * 批量查询促销信息
	 * @return
	 */
	public String queryPromotions(){
		try{
			List<PromotionBO> promotionListInfoList = iBackPromotionService.queryPromotionByPageNum(1, 20);
	    	Map<String, Object> parmKeyValue = new HashMap<String, Object>();
	    	parmKeyValue.put("promotionListInfoList",promotionListInfoList);
	    	putParamToVm(parmKeyValue);
		}catch(Exception e){
			logger.error("PromotionAction.queryPromotions查询促销信息异常！！！", e);
		}
		return "queryPromotions";
	}
	
	/**
	 * 按照条件查询促销信息
	 * @return
	 */
	public String queryPromotionByCondition(){
		BackPromotionResult backPromotionResult = new BackPromotionResult();
		try{
			PromotionBO promotionBO = new PromotionBO();
			promotionBO.setId(promotionId);
			promotionBO.setSkuId(skuId);
			promotionBO.setSkuNum(skuNum);
			if(yn!=null){
				promotionBO.setYn(yn==1?true:false);
			}
			List<PromotionBO> promotionListInfoList = iBackPromotionService.queryPromotionByCondition(promotionBO);
			backPromotionResult.setPromotionListInfoList(promotionListInfoList);
			backPromotionResult.setSuccess(true);
			print(JsonUtil.toJson(backPromotionResult));
		}catch(Exception e){
			logger.error("PromotionAction.queryPromotionByCondition查询促销信息异常！！！上下文参数={promotionId="+promotionId+",  skuId="+skuId+",  skuNum="+skuNum+",  yn="+yn+"}", e);
			backPromotionResult.setSuccess(false);
			backPromotionResult.setMsg("查询异常！！！");
			print(JsonUtil.toJson(backPromotionResult));
		}
		return "queryPromotionByCondition";
	}
	
	/**
	 * 删除(置为无效)促销信息
	 * @return
	 */
	public String delPromotion(){
		BackPromotionResult backPromotionResult = new BackPromotionResult();
		try{
			PromotionBO promotionBO = new PromotionBO();
			promotionBO.setId(promotionId);
			promotionBO.setSkuId(skuId);
			promotionBO.setSkuNum(skuNum);
			promotionBO.setYn(false);
			boolean delResult = iBackPromotionService.updatePromotion(promotionBO);
			if(delResult){
				backPromotionResult.setSuccess(true);
				backPromotionResult.setMsg("置为失效成功！！！");
				print(JsonUtil.toJson(backPromotionResult));
			}else{
				backPromotionResult.setSuccess(false);
				backPromotionResult.setMsg("置为失效失败！！！");
				print(JsonUtil.toJson(backPromotionResult));
			}
		}catch(Exception e){
			logger.error("PromotionAction.delPromotion将促销信息置为失效异常！！！上下文参数={promotionId="+promotionId+",  skuId="+skuId+",  skuNum="+skuNum+",  yn="+yn+"}", e);
			backPromotionResult.setSuccess(false);
			backPromotionResult.setMsg("置为无效异常！！！");
			print(JsonUtil.toJson(backPromotionResult));
		}
		return "delPromotion";
	}

	/**
	 * 更新促销信息
	 * @return
	 */
	public String updatePromotion(){
		BackPromotionResult backPromotionResult = new BackPromotionResult();
		try{
			PromotionBO promotionBO = new PromotionBO();
			promotionBO.setId(promotionId);
			promotionBO.setSkuId(skuId);
			promotionBO.setSkuNum(skuNum);
			promotionBO.setYn(yn==1?true:false);
			boolean delResult = iBackPromotionService.updatePromotion(promotionBO);
			if(delResult){
				backPromotionResult.setSuccess(true);
				backPromotionResult.setMsg("更新成功！！！");
				print(JsonUtil.toJson(backPromotionResult));
			}else{
				backPromotionResult.setSuccess(false);
				backPromotionResult.setMsg("更新失败！！！");
				print(JsonUtil.toJson(backPromotionResult));
			}
		}catch(Exception e){
			logger.error("PromotionAction.updatePromotion更新促销信息置异常！！！上下文参数={promotionId="+promotionId+",  skuId="+skuId+",  skuNum="+skuNum+",  yn="+yn+"}", e);
			backPromotionResult.setSuccess(false);
			backPromotionResult.setMsg("更新异常！！！");
			print(JsonUtil.toJson(backPromotionResult));
		}
		return "updatePromotion";
	}
	
	/**
	 * 物理删除
	 * @return
	 */
	public String physicalDel(){
		return EXCEPTION;
	}


	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Integer getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(Integer skuNum) {
		this.skuNum = skuNum;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public void setiBackPromotionService(IBackPromotionService iBackPromotionService) {
		this.iBackPromotionService = iBackPromotionService;
	}
}
