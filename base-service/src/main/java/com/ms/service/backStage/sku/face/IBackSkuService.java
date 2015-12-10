package com.ms.service.backStage.sku.face;

import java.util.List;

import com.ms.domain.sku.bo.SkuBO;

/**
 * 操作商品信息服务类
 * @author zhoushanjie
 *
 */
public interface IBackSkuService {
	
	/**
	 * 添加商品
	 * @param SkuBO
	 * @return
	 */
	boolean addSku(SkuBO skuBO);
	
	/**
	 * 更新商品信息
	 * @param SkuBO
	 * @return
	 */
	boolean updateSkuInfo(SkuBO skuBO);
	
	/**
	 * 删除商品信息
	 * @param id
	 * @return
	 */
	boolean delSkuInfo(Long id);
	
	/**
	 * 根据商品id查询商品信息
	 * @param skuId
	 * @return
	 */
	SkuBO querySkuById(Long skuId);
	
	/**
	 * 根据商品id列表批量查询商品信息
	 * @param skuIds
	 * @return
	 */
	List<SkuBO> querySkuListByIds(List<Long> skuIds);
	
	/**
	 * 根据分页查询商品信息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<SkuBO> querySkuListByPageNum(int page,int pageSize);

}
