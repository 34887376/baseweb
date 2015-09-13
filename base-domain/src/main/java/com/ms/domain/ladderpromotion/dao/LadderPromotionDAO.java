package com.ms.domain.ladderpromotion.dao;

import java.io.Serializable;

public class LadderPromotionDAO implements Serializable {
	
	private static final long serialVersionUID = -4179399505022261079L;

	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 促销id
	 */
	private Long promotionId;
	
	/**
	 * 阶梯促销规则id
	 */
	private Long ladderId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Long getLadderId() {
		return ladderId;
	}

	public void setLadderId(Long ladderId) {
		this.ladderId = ladderId;
	}

}
