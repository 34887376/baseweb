package com.ms.domain.action.backstage;

import java.util.List;

import com.ms.domain.action.base.BaseActionResult;
import com.ms.domain.ladderpromotion.bo.LadderPromotionBO;

public class BackLadderPromotionResult extends BaseActionResult{

	private static final long serialVersionUID = 821491676794679694L;

	/**
	 * 阶梯信息
	 */
	List<LadderPromotionBO> ladderPromotionBOList;

	public List<LadderPromotionBO> getLadderPromotionBOList() {
		return ladderPromotionBOList;
	}

	public void setLadderPromotionBOList(
			List<LadderPromotionBO> ladderPromotionBOList) {
		this.ladderPromotionBOList = ladderPromotionBOList;
	}

	
}
