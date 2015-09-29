package com.ms.domain.promotion.bo;
import java.io.Serializable;
import java.util.List;

import com.ms.domain.ladderpromotion.bo.LadderPromotionInfoBO;
import com.ms.domian.action.promotion.vo.LadderPromotionInfoVO;

/**
 * 促销整体信息
 * @author zhoushanjie
 *
 */
public class PromotionInfoBO implements Serializable {

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
	
}
