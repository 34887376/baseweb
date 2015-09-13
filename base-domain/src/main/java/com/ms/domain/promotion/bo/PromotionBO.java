package com.ms.domain.promotion.bo;

import java.io.Serializable;

public class PromotionBO implements Serializable {

	private static final long serialVersionUID = 6817221000574046543L;
	
	/**
	 * 促销id
	 */
	private long id;

	/**
	 * 商品id
	 */
	private long skuId;
	
	/**
	 * 促销的商品数量
	 */
	private int skuNum;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSkuId() {
		return skuId;
	}

	public void setSkuId(long skuId) {
		this.skuId = skuId;
	}

	public int getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}
}
