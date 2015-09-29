package com.ms.dao.promotion.face;

import java.util.List;

import com.ms.domain.promotion.dao.PromotionDAO;

/**
 * 管理促销信息
 * @author zhoushanjie
 *
 */
public interface IPromotionDAO {
	
	/**
	 * 根据促销id获取促销信息
	 * @param id
	 * @return
	 */
	PromotionDAO queryPromotionById(Long id) throws Exception;
	
	/**
	 * 根据促销id列表批量获取促销
	 * @param idList
	 * @return
	 */
	List<PromotionDAO> queryPromotionsByIds(List<Long> idList) throws Exception;
	
	/**
	 *  添加促销信息
	 * @param promotionDAO
	 * @return
	 */
	long addPromotion(PromotionDAO promotionDAO) throws Exception;
	
	/**
	 * 批量删除促销信息
	 * @param idList
	 * @return
	 */
	boolean delPromotionsByIds(List<Long> idList) throws Exception;
	
	/**
	 * 修改促销信息
	 * @param promotionDAO
	 * @return
	 */
	boolean updatePromotion(PromotionDAO promotionDAO) throws Exception;
	

}
