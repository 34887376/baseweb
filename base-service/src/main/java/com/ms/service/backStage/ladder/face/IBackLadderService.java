package com.ms.service.backStage.ladder.face;

import java.util.List;

import com.ms.domain.ladder.bo.LadderBO;

public interface IBackLadderService {

	/**
	 * 根据分页批量查询阶梯分类信息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<LadderBO> queryLadderByPageNum(int page, int pageSize);
	
	/**
	 * 根据条件查询阶梯分类信息
	 * @param ladderBO
	 * @return
	 */
	public List<LadderBO> queryLadderByCondition(LadderBO ladderBO);
	
	/**
	 * 删除阶梯分类信息
	 * @param idList
	 * @return
	 */
	public boolean delLadder(List<Long> idList);
	
	/**
	 * 更新阶梯分类信息
	 * @param ladderBO
	 * @return
	 */
	public boolean updateLadder(LadderBO ladderBO);
	
	/**
	 * 添加阶梯分类信息
	 * @param ladderBO
	 * @return
	 */
	public boolean addLadder(LadderBO ladderBO);
	
	
}
