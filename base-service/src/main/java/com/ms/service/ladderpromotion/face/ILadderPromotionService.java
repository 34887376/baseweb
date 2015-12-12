package com.ms.service.ladderpromotion.face;

import java.util.List;
import java.util.Map;

import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;

public interface ILadderPromotionService {

	/**
	 * 根据id查询阶梯促销关系
	 * @param ladderId
	 * @return
	 */
	List<LadderPromotionBO> queryLadderPromotionByPromotionId(long promotionId);
	
	/**
	 * 根据promotionidlist查询阶梯促销关系
	 * @param ladderIds
	 * @return
	 */
	Map<Long,List<LadderPromotionBO>> queryLadderPromotionByPromotionIds(List<Long> promotionIds);
}
