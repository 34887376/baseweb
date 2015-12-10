package com.ms.domain.promotion.bo;

import java.io.Serializable;

public class PromotionBO implements Serializable {

	private static final long serialVersionUID = 858241821185021952L;

	/**
	 * 促销id
	 */
	private Long id;

	/**
	 * 商品id
	 */
	private Long skuId;
	
	/**
	 * 促销的商品数量
	 */
	private Integer skuNum;
	
	/**
	 * 是否有效
	 */
	private Boolean yn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Integer getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(Integer skuNum) {
		this.skuNum = skuNum;
	}

	public Boolean getYn() {
		return yn;
	}

	public void setYn(Boolean yn) {
		this.yn = yn;
	}
}
