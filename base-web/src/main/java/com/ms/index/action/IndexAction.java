package com.ms.index.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ms.domain.promotion.bo.PromotionBriefBOInfo;
import com.ms.domain.promotion.bo.PromotionDetailInfoBO;
import com.ms.domian.action.promotion.vo.PromotionBriefVOInfo;
import com.ms.service.promotion.face.IPromotionService;

import base.test.base.action.BaseAction;
import base.test.base.util.JsonUtil;

public class IndexAction extends BaseAction{

	private static final long serialVersionUID = 1L;
	
	private IPromotionService iPromotionService;
	
    public String showIndex() {
    	List<PromotionBriefBOInfo> promotionInfoList = iPromotionService.queryLastPromotionInfoForIndex(8);
    	
    	String promotionInfoStr = JsonUtil.toJson(promotionInfoList);
    	Map<String, Object> parmKeyValue = new HashMap<String, Object>();
    	parmKeyValue.put("promotionInfoList",promotionInfoList);
    	putParamToVm(parmKeyValue);
        return SUCCESS;
    }


	public void setiPromotionService(IPromotionService iPromotionService) {
		this.iPromotionService = iPromotionService;
	}

}
