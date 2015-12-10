package com.ms.domain.action.backstage;

import java.util.List;

import com.ms.domain.action.base.BaseActionResult;
import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.sku.bo.SkuBO;

public class BackPromotionResult extends BaseActionResult{

	private static final long serialVersionUID = 2018803030254185016L;
	
	/**
	 * 促销信息
	 */
	List<PromotionBO> promotionListInfoList;

	public List<PromotionBO> getPromotionListInfoList() {
		return promotionListInfoList;
	}

	public void setPromotionListInfoList(List<PromotionBO> promotionListInfoList) {
		this.promotionListInfoList = promotionListInfoList;
	}

}
