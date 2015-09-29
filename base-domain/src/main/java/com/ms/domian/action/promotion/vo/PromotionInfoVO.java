package com.ms.domian.action.promotion.vo;
import java.io.Serializable;
import java.util.List;

/**
 * 返回给前台页面的促销信息
 * @author zhoushanjie
 *
 */
public class PromotionInfoVO implements Serializable {

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
	
}
