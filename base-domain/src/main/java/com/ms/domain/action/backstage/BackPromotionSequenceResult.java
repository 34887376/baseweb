package com.ms.domain.action.backstage;

import java.util.List;

import com.ms.domain.action.base.BaseActionResult;
import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;

public class BackPromotionSequenceResult extends BaseActionResult{

	private static final long serialVersionUID = 821491676794679694L;

	/**
	 * 阶梯信息
	 */
	List<PromotionSequenceBO> promotionSequenceBOList;

	public List<PromotionSequenceBO> getPromotionSequenceBOList() {
		return promotionSequenceBOList;
	}

	public void setPromotionSequenceBOList(
			List<PromotionSequenceBO> promotionSequenceBOList) {
		this.promotionSequenceBOList = promotionSequenceBOList;
	}

	
	
}
