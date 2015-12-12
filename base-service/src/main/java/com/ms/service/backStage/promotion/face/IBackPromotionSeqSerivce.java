package com.ms.service.backStage.promotion.face;

import java.util.List;

import com.ms.domain.promotionsequence.bo.PromotionSequenceBO;

public interface IBackPromotionSeqSerivce {
	
	/**
	 * 后台刷新促销信息到redis中
	 */
	boolean refreshBackPromotionInfoToRedis(long startPromotionId);
	
	
	/**
	 * 后台修改或者添加一条促销信息后，从redis中删除
	 */
	void deletePromotionFromRedis();
	
	/**
	 * 根据分页查询促销排序信息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PromotionSequenceBO> queryPromotionSequenceByPageNum(int page, int pageSize);
	
	/**
	 * 根据条件查询促销排序信息
	 * @param promotionSequenceBO
	 * @return
	 */
	List<PromotionSequenceBO> queryPromotionSequenceByCondition(PromotionSequenceBO promotionSequenceBO);
	
	/**
	 * 添加促销排序信息
	 * @param promotionSequenceBO
	 * @return
	 */
	boolean addPromotionSequence(PromotionSequenceBO promotionSequenceBO);
	
	/**
	 * 删除促销排序信息
	 * @param promotionSequenceBO
	 * @return
	 */
	boolean delPromotionSequence(List<Long> idList);
	
	/**
	 * 更新促销排序信息
	 * @param promotionSequenceBO
	 * @return
	 */
	boolean updatePromotionSequence(PromotionSequenceBO promotionSequenceBO);
}
