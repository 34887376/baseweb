package com.ms.service.backStage.ladder.face;

import java.util.List;

import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;

public interface IBackLadderPromotionService {

	/**
	 * 根据页数查询阶梯促销信息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<LadderPromotionBO> queryLadderPromotionByPageNum(int page, int pageSize);
	
	/**
	 * 根据条件综合查询阶梯促销信息
	 * @param ladderPromotionBO
	 * @return
	 */
	List<LadderPromotionBO> queryLadderPromotionByCondition(LadderPromotionBO ladderPromotionBO);
	
	/**
	 * 添加阶梯促销信息
	 * @param ladderPromotionBO
	 * @return
	 */
	boolean addLadderPromotion(LadderPromotionBO ladderPromotionBO);
	
	/**
	 * 更新阶梯促销信息
	 * @param ladderPromotionBO
	 * @return
	 */
	boolean updateLadderPromotion(LadderPromotionBO ladderPromotionBO);
	
	/**
	 * 删除阶梯促销信息
	 * @param idList
	 * @return
	 */
	boolean delLadderPromotion(List<Long> idList);
}
