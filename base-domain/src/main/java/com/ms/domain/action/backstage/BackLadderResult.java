package com.ms.domain.action.backstage;

import java.util.List;

import com.ms.domain.action.base.BaseActionResult;
import com.ms.domain.ladder.bo.LadderBO;

public class BackLadderResult extends BaseActionResult{

	private static final long serialVersionUID = 821491676794679694L;

	/**
	 * 阶梯信息
	 */
	List<LadderBO> ladderList;

	public List<LadderBO> getLadderList() {
		return ladderList;
	}

	public void setLadderList(List<LadderBO> ladderList) {
		this.ladderList = ladderList;
	}
	
	
	
}
