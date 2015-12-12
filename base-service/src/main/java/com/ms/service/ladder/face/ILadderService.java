package com.ms.service.ladder.face;

import java.util.List;

import com.ms.domain.ladder.bo.LadderBO;

public interface ILadderService {

	/**
	 * 根据id查询阶梯规则
	 * @param ladderId
	 * @return
	 */
	LadderBO queryLadderById(long ladderId);
	
	/**
	 * 根据idlist查询阶梯规则列表
	 * @param ladderIds
	 * @return
	 */
	List<LadderBO> queryLadderByIds(List<Long> ladderIds);
}
