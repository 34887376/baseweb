package com.ms.dao.promotionsequence.face;

import java.util.List;

import com.ms.domain.promotionsequence.dao.PromotionSequenceDAO;

public interface IPromotionSequenceDAO {

	/**
	 * 添加促销排序信息
	 * @param promotionSequenceDAO
	 * @return
	 */
	long addPromotionSequence(PromotionSequenceDAO promotionSequenceDAO) throws Exception;
	
	/**
	 * 更新促销排序信息
	 * @param promotionSequenceDAO
	 * @return
	 */
	boolean updatePromotionSequence(PromotionSequenceDAO promotionSequenceDAO) throws Exception;
	
	/**
	 * 根据条件删除促销排序信息
	 * @param promotionSequenceDAO
	 * @return
	 */
	boolean delPromotionSequence(PromotionSequenceDAO promotionSequenceDAO) throws Exception;
	
	/**
	 * 根据条件查询促销排序信息
	 * @param promotionSequenceDAO
	 * @return
	 */
	List<PromotionSequenceDAO> queryPromotionSequence(PromotionSequenceDAO promotionSequenceDAO) throws Exception;
}
