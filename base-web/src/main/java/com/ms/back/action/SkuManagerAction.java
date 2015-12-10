package com.ms.back.action;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ms.action.constant.PageNumConstant;
import com.ms.domain.action.backstage.BackSkuResult;
import com.ms.domain.sku.bo.SkuBO;
import com.ms.service.backStage.sku.face.IBackSkuService;

import base.test.base.action.BaseAction;
import base.test.base.util.JsonUtil;

public class SkuManagerAction extends BaseAction{

	private static final long serialVersionUID = -2263571560482626290L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	//商品id
	private Long skuId;
	//商品数量
	private Integer skuNum;
	//商品名称
	private String skuName;
	//商品广告词
	private String skuAdverst;

	private Double inprice;
	
	private Double outprice;
	
	private Integer isvaliade;
	
	private File upfileId;
	
	private IBackSkuService iBackSkuService;
	
	public String querySkus(){
		List<SkuBO> skuList = iBackSkuService.querySkuListByPageNum(PageNumConstant.PAGE, PageNumConstant.PAGE_SIZE);
    	Map<String, Object> parmKeyValue = new HashMap<String, Object>();
    	parmKeyValue.put("skuList",skuList);
    	putParamToVm(parmKeyValue);
		return "querySuccess";
	}
	
	/**
	 * 根据条件查询商品信息
	 * @return
	 */
	public String querySkuByCondition(){
		try{
			 SkuBO querySku = iBackSkuService.querySkuById(skuId);
			 List<SkuBO> listInfo = new ArrayList<SkuBO>();
			 listInfo.add(querySku);
			 if(StringUtils.isNotBlank(querySku.getName())){
				 BackSkuResult result= new BackSkuResult();
				 result.setSkuListInfoList(listInfo);
				 result.setSuccess(true);
				 result.setMsg("查询成功！！！");
				 print(JsonUtil.toJson(result));
				 return "querySkuByCondition";
			}else{
				BackSkuResult result= new BackSkuResult();
				result.setSuccess(false);
				result.setMsg("查询失败！！！");
				print(JsonUtil.toJson(result));
				return "querySkuByCondition";
			}
		}catch(Exception e){
			logger.error("SkuManagerAction.querySkuByCondition查询商品信息异常！！！上下文参数={skuId="+skuId+"}", e);
			return EXCEPTION;
		}
	}


	/**
	 * 添加商品信息
	 * @return
	 */
	public String addSkus(){
		try{
			SkuBO skuBO = new SkuBO();
			skuBO.setAdverst(skuAdverst);
			skuBO.setInprice(new BigDecimal(inprice));
			skuBO.setName(skuName);
			skuBO.setNum(skuNum);
			skuBO.setOutprice(new BigDecimal(outprice));
			skuBO.setYn(isvaliade==1? true:false);
			
			boolean addResult = iBackSkuService.addSku(skuBO);
			if(addResult){
				BackSkuResult result= new BackSkuResult();
				result.setSuccess(true);
				result.setMsg("添加成功！！！");
				print(JsonUtil.toJson(result));
				return "addSuccess";
			}else{
				BackSkuResult result= new BackSkuResult();
				result.setSuccess(false);
				result.setMsg("添加失败！！！");
				print(JsonUtil.toJson(result));
				return "addSuccess";
			}
		}catch(Exception e){
			logger.error("SkuManagerAction.addSkus添加商品信息异常！！！上下文参数={userName="+skuAdverst+"}", e);
			return EXCEPTION;
		}

	}
	
	public String updateSkus(){
		try{
			SkuBO skuBO = new SkuBO();
			skuBO.setId(skuId);
			skuBO.setAdverst(skuAdverst);
			skuBO.setInprice(new BigDecimal(inprice));
			skuBO.setName(skuName);
			skuBO.setNum(skuNum);
			skuBO.setOutprice(new BigDecimal(outprice));
			skuBO.setYn(isvaliade==1? true:false);
			
			boolean upadateResult = iBackSkuService.updateSkuInfo(skuBO);
			if(upadateResult){
				BackSkuResult result= new BackSkuResult();
				result.setSuccess(true);
				result.setMsg("更新成功！！！");
				print(JsonUtil.toJson(result));
				return "updateSkus";
			}else{
				BackSkuResult result= new BackSkuResult();
				result.setSuccess(false);
				result.setMsg("更新失败！！！");
				print(JsonUtil.toJson(result));
				return "updateSkus";
			}
		}catch(Exception e){
			logger.error("SkuManagerAction.updateSkus更新商品信息异常！！！上下文参数={skuId="+skuId+"}", e);
			return EXCEPTION;
		}

	}
	
	public String delSkus(){
		try{
			SkuBO skuBO = new SkuBO();
			skuBO.setId(skuId);
			skuBO.setYn(false);
			boolean delResult = iBackSkuService.updateSkuInfo(skuBO);
//			boolean delResult = iSkuManagerService.delSkuInfo(skuId);
			if(delResult){
				BackSkuResult result= new BackSkuResult();
				result.setSuccess(true);
				result.setMsg("删除成功！！！");
				print(JsonUtil.toJson(result));
				return "delSkus";
			}else{
				BackSkuResult result= new BackSkuResult();
				result.setSuccess(false);
				result.setMsg("删除失败！！！");
				print(JsonUtil.toJson(result));
				return "delSkus";
			}
		}catch(Exception e){
			logger.error("SkuManagerAction.delSkus删除商品信息异常！！！上下文参数={skuId="+skuId+"}", e);
			return EXCEPTION;
		}
	}
	
	/**
	 * 物理删除
	 * @return
	 */
	public String physicalDel(){
		return EXCEPTION;
	}
	
	public File getUpfileId() {
		return upfileId;
	}



	public void setUpfileId(File upfileId) {
		this.upfileId = upfileId;
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

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getSkuAdverst() {
		return skuAdverst;
	}

	public void setSkuAdverst(String skuAdverst) {
		this.skuAdverst = skuAdverst;
	}

	public Double getInprice() {
		return inprice;
	}

	public void setInprice(Double inprice) {
		this.inprice = inprice;
	}

	public Double getOutprice() {
		return outprice;
	}

	public void setOutprice(Double outprice) {
		this.outprice = outprice;
	}

	public Integer getIsvaliade() {
		return isvaliade;
	}

	public void setIsvaliade(Integer isvaliade) {
		this.isvaliade = isvaliade;
	}
	
	public static void main(String[] args) {
		 Long id=100020151202001L;
		 SkuBO skuBO = new SkuBO();
//		 skuBO.setId(100020151202001L);
		 skuBO.setId(id);
		 System.out.println(JsonUtil.toJson(skuBO));
	}

	public void setiBackSkuService(IBackSkuService iBackSkuService) {
		this.iBackSkuService = iBackSkuService;
	}

}
