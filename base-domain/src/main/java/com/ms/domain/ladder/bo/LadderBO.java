package com.ms.domain.ladder.bo;

import java.io.Serializable;
import java.math.BigDecimal;

public class LadderBO implements Serializable {

	private static final long serialVersionUID = 3612098432966007895L;

	/**
	 * 阶梯促销规则id
	 */
	private Long id;
	
	/**
	 * 价格折扣比率(商品原价的)
	 */
	private BigDecimal priceDiscount;
	
	/**
	 * 商品数量占比(促销商品总数的百分比)
	 */
	private BigDecimal numPercent;
	
	/**
	 * 阶梯类型
	 */
	private Integer type;
	
	/**
	 * 是否有效
	 */
	private Boolean yn;


	public BigDecimal getPriceDiscount() {
		return priceDiscount;
	}

	public void setPriceDiscount(BigDecimal priceDiscount) {
		this.priceDiscount = priceDiscount;
	}

	public BigDecimal getNumPercent() {
		return numPercent;
	}

	public void setNumPercent(BigDecimal numPercent) {
		this.numPercent = numPercent;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getYn() {
		return yn;
	}

	public void setYn(Boolean yn) {
		this.yn = yn;
	}
}
