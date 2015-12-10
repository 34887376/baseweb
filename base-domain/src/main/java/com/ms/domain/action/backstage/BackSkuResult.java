package com.ms.domain.action.backstage;

import java.util.List;

import com.ms.domain.action.base.BaseActionResult;
import com.ms.domain.sku.bo.SkuBO;

public class BackSkuResult extends BaseActionResult{

	private static final long serialVersionUID = 2018803030254185016L;
	
	/**
	 * 商品信息
	 */
	List<SkuBO> skuListInfoList;

	public List<SkuBO> getSkuListInfoList() {
		return skuListInfoList;
	}

	public void setSkuListInfoList(List<SkuBO> skuListInfoList) {
		this.skuListInfoList = skuListInfoList;
	}


	

}
