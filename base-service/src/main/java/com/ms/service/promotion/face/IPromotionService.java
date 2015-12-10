package com.ms.service.promotion.face;

import java.util.List;

import com.ms.domain.promotion.bo.PromotionBO;
import com.ms.domain.promotion.bo.PromotionInfoBO;

public interface IPromotionService {
	
	/**
	 * 根据促销id获取促销信息
	 * @param id
	 * @return
	 */
	PromotionBO queryPromotionById(Long id);
	
	/**
	 * 根据促销id列表批量获取促销
	 * @param idList
	 * @return
	 */
	List<PromotionBO> queryPromotionsByIds(List<Long> idList);
	
	/**
	 *  添加促销信息
	 * @param promotionDAO
	 * @return
	 */
	long addPromotion(PromotionBO promotionBO);
	
	/**
	 * 批量删除促销信息
	 * @param idList
	 * @return
	 */
	boolean delPromotionsByIds(List<Long> idList);
	
	/**
	 * 修改促销信息
	 * @param promotionDAO
	 * @return
	 */
	boolean updatePromotion(PromotionBO promotionBO);
	
	/**
	 * 查询即将促销的前num条促销信息
	 * @param num
	 * @return
	 */
	List<PromotionInfoBO> queryLastNumPromotion(int num);
	
}
