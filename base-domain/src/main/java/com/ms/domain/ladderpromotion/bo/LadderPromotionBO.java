package com.ms.domain.ladderpromotion.bo;

import java.io.Serializable;

public class LadderPromotionBO implements Serializable {
	
	private static final long serialVersionUID = -4179399505022261079L;

	/**
	 * 主键id
	 */
	private Long ladderPromotionId;
	
	/**
	 * 促销id
	 */
	private Long promotionId;
	
	/**
	 * 阶梯促销规则id
	 */
	private Long ladderId;
	
	/**
	 * 是否有效
	 */
	private Boolean yn;

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

	public Long getLadderPromotionId() {
		return ladderPromotionId;
	}

	public void setLadderPromotionId(Long ladderPromotionId) {
		this.ladderPromotionId = ladderPromotionId;
	}

	public Boolean getYn() {
		return yn;
	}

	public void setYn(Boolean yn) {
		this.yn = yn;
	}

}
