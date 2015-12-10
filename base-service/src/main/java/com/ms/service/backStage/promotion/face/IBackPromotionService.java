package com.ms.service.backStage.promotion.face;

import java.util.List;

import com.ms.domain.promotion.bo.PromotionBO;

public interface IBackPromotionService {

	/**
	 * 批量查询促销信息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PromotionBO> queryPromotionByPageNum(int page, int pageSize);
	
	/**
	 * 根据条件查询促销信息
	 * @param promotionBO
	 * @return
	 */
	List<PromotionBO> queryPromotionByCondition(PromotionBO promotionBO);
	
	/**
	 * 添加促销信息
	 * @param promotionBO
	 * @return
	 */
	boolean addPromotion(PromotionBO promotionBO);
	
	/**
	 * 更新促销信息
	 * @param promotionBO
	 * @return
	 */
	boolean updatePromotion(PromotionBO promotionBO);
	
	/**
	 * 物理删除促销信息
	 * @param idList
	 * @return
	 */
	boolean delPromotion(List<Long> idList);
}
