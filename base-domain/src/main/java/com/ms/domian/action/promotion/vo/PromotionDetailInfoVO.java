package com.ms.domian.action.promotion.vo;
import java.io.Serializable;
import java.util.List;

/**
 * 返回给前台页面的促销信息
 * @author zhoushanjie
 *
 */
public class PromotionDetailInfoVO implements Serializable {

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
	 * 促销的状态，参考PromotionStatusConstant
	 */
	private Integer status;
	
	/**
	 * 促销包含的阶梯信息
	 */
	private List<LadderPromotionInfoVO> ladderList;

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

	public List<LadderPromotionInfoVO> getLadderList() {
		return ladderList;
	}

	public void setLadderList(List<LadderPromotionInfoVO> ladderList) {
		this.ladderList = ladderList;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public String getSkuImgUrl() {
		return skuImgUrl;
	}

	public void setSkuImgUrl(String skuImgUrl) {
		this.skuImgUrl = skuImgUrl;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
