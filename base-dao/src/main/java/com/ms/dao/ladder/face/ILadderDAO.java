package com.ms.dao.ladder.face;

import java.util.List;

import com.ms.domain.ladder.dao.LadderDAO;

public interface ILadderDAO {
	
	/**
	 * 添加阶梯促销规则
	 * @param ladderDAO
	 * @return
	 */
	long addLadder(LadderDAO ladderDAO) throws Exception;
	
	/**
	 * 根据id删除阶梯促销
	 * @param id
	 * @return
	 */
	boolean delLadder(Long id) throws Exception;

	/**
	 * 根据id更新促销信息
	 * @param ladderDAO
	 * @return
	 */
	boolean updateLadder(LadderDAO ladderDAO) throws Exception;
	
	/**
	 * 根据id查询阶梯规则信息
	 * @param id
	 * @return
	 */
	LadderDAO queryLadderById(Long id) throws Exception;
	
	/**
	 * 根据id查询阶梯促销规则
	 * @param idList
	 * @return
	 */
	List<LadderDAO> queryLadderList(List<Long> idList) throws Exception;
	
	/**
	 * 查询所有的阶梯促销规则
	 * @return
	 */
	List<LadderDAO> queryAllLadder() throws Exception;
	
	/**
	 * 根据分页查询阶梯规则信息
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	List<LadderDAO> queryLadderListByPageNum(int page, int pageSize)
			throws Exception;
	
	/**
	 * 根据组合规则查询阶梯规则信息
	 * @param ladderDAO
	 * @return
	 * @throws Exception
	 */
	List<LadderDAO> queryLadderListByCondition(LadderDAO ladderDAO) throws Exception;
}
