package com.ms.dao.ladderpromotion.face;

import java.util.List;

import com.ms.domain.ladderpromotion.dao.LadderPromotionDAO;

public interface ILadderPromotion {

	/**
	 * 添加阶梯促销组合
	 * @param ladderPromotionDAO
	 * @return
	 */
	long addLadderPromotion(LadderPromotionDAO ladderPromotionDAO) throws Exception;
	
	/**
	 * 修改阶梯促销组合
	 * @param ladderPromotionDAO
	 * @return
	 */
	boolean updateLadderPromotion(LadderPromotionDAO ladderPromotionDAO) throws Exception;
	
	/**
	 * 删除阶梯促销组合
	 * @param ladderPromotionDAO
	 * @return
	 */
	boolean delLadderPromotion(LadderPromotionDAO ladderPromotionDAO) throws Exception;
	
	/**
	 * 根据不同的条件查询符合条件的阶梯促销组合
	 * @param ladderPromotionDAO
	 * @return
	 */
	List<LadderPromotionDAO> queryLadderPromoitonByCondition(LadderPromotionDAO ladderPromotionDAO) throws Exception;
}
