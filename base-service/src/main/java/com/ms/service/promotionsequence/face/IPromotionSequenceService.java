package com.ms.service.promotionsequence.face;

import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;

public interface IPromotionSequenceService {

	/**
	 * 根据条件查询促销排序信息
	 * @param promotionSequenceBO
	 * @return
	 */
	PromotionSequenceBO queryPromotionSequenceByPromotionId(long promotionId);
	
	
	/**
	 * 根据促销排序id查询促销排序信息
	 * @param promotionSequenceId
	 * @return
	 */
	PromotionSequenceBO queryPromotionSequenceById(long promotionSequenceId);
}
