package com.ms.domian.action.promotion.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 展示给用户的阶梯促销信息
 * @author zhoushanjie
 *
 */
public class LadderPromotionInfoVO implements Serializable {

	private static final long serialVersionUID = 8243215622936055617L;

	/**
	 * 阶梯促销id
	 */
	private Long ladderPromotionId;
	
	/**
	 * 促销的商品数量
	 */
	private int skuNum;
	
	/**
	 * 促销价格
	 */
	private BigDecimal promotionPrice;
	
	/**
	 * 折扣力度
	 */
	private String discount;


	public int getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}

	public BigDecimal getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(BigDecimal promotionPrice) {
		this.promotionPrice = promotionPrice;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public Long getLadderPromotionId() {
		return ladderPromotionId;
	}

	public void setLadderPromotionId(Long ladderPromotionId) {
		this.ladderPromotionId = ladderPromotionId;
	}
}
