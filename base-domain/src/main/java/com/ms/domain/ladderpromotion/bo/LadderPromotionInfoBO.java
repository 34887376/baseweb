package com.ms.domain.ladderpromotion.bo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 阶梯促销组合信息
 * @author zhoushanjie
 *
 */
public class LadderPromotionInfoBO implements Serializable {

	private static final long serialVersionUID = 8243215622936055617L;

	/**
	 * 阶梯促销id
	 */
	private Long promotionLadderId;
	
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
	
	/**
	 * 阶梯类型，一等奖，二等奖，三等奖
	 */
	private int type;

	public Long getPromotionLadderId() {
		return promotionLadderId;
	}

	public void setPromotionLadderId(Long promotionLadderId) {
		this.promotionLadderId = promotionLadderId;
	}

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
}
