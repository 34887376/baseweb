package com.ms.domain.promotion.bo;
import java.io.Serializable;
import java.util.List;

import com.ms.domain.ladderpromotion.bo.LadderPromotionInfoBO;

/**
 * 促销整体信息
 * @author zhoushanjie
 *
 */
public class PromotionDetailInfoBO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	private long skuId;
	
	/**
	 * 商品名称
	 */
	private String skuName;
	
	/**
	 * 商品图片链接
	 */
	private String skuImgUrl;
	
	/**
	 * 广告促销语
	 */
	private String advert;
	
	/**
	 * 促销id
	 */
	private Long promotionId;
	
	/**
	 * 促销的状态，参考PromotionStatusConstant
	 */
	private Integer status;
	
	/**
	 * 促销包含的阶梯信息
	 */
	private List<LadderPromotionInfoBO> ladderPromotionList;

	public long getSkuId() {
		return skuId;
	}

	public void setSkuId(long skuId) {
		this.skuId = skuId;
	}

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

	public List<LadderPromotionInfoBO> getLadderPromotionList() {
		return ladderPromotionList;
	}

	public void setLadderPromotionList(
			List<LadderPromotionInfoBO> ladderPromotionList) {
		this.ladderPromotionList = ladderPromotionList;
	}

	public String getSkuImgUrl() {
		return skuImgUrl;
	}

	public void setSkuImgUrl(String skuImgUrl) {
		this.skuImgUrl = skuImgUrl;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
