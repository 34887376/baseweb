package com.ms.service.sku.face;

import java.util.List;

import com.ms.domain.sku.bo.SkuBO;

public interface ISkuService {

	/**
	 * 根据商品id查询商品信息
	 * @param skuId
	 * @return
	 */
	SkuBO querySkuById(long skuId);
	
	/**
	 * 根据商品idlist查询商品信息列表
	 * @param skuIdList
	 * @return
	 */
	List<SkuBO> querySkuByIds(List<Long> skuIdList);
}
