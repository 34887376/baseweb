package com.ms.domian.action.promotion.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class PromotionBriefVOInfo  implements Serializable{

	private static final long serialVersionUID = -2566118902129506524L;

	/**
	 * 商品名称
	 */
	private String skuName;
	
	/**
	 * 商品数量
	 */
	private int skuNum;
	
	/**
	 * 广告促销语
	 */
	private String advert;
	
	/**
	 * 商品图片链接
	 */
	private String skuImgUrl;
	
	/**
	 * 促销id
	 */
	private Long promotionId;
	
	/**
	 * 最低促销价格
	 */
	private BigDecimal cheapestPromotionPrice;
	
	/**
	 * 最大的折扣力度
	 */
	private BigDecimal topestDisconuntPercent;
	
	/**
	 * 促销的状态，参考PromotionStatusConstant
	 */
	private Integer status;

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getAdvert() {
		return advert;
	}

	public void setAdvert(String advert) {
		this.advert = advert;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public BigDecimal getCheapestPromotionPrice() {
		return cheapestPromotionPrice;
	}

	public void setCheapestPromotionPrice(BigDecimal cheapestPromotionPrice) {
		this.cheapestPromotionPrice = cheapestPromotionPrice;
	}

	public BigDecimal getTopestDisconuntPercent() {
		return topestDisconuntPercent;
	}

	public void setTopestDisconuntPercent(BigDecimal topestDisconuntPercent) {
		this.topestDisconuntPercent = topestDisconuntPercent;
	}

	public int getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSkuImgUrl() {
		return skuImgUrl;
	}

	public void setSkuImgUrl(String skuImgUrl) {
		this.skuImgUrl = skuImgUrl;
	}
	
}
