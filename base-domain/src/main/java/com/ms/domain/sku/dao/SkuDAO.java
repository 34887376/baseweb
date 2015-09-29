package com.ms.domain.sku.dao;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品信息
 * @author zhoushanjie
 *
 */
public class SkuDAO implements Serializable {
	

	private static final long serialVersionUID = -6582462404378104043L;

	/**
	 * 商品id
	 */
	private Long id;
	
	/**
	 * 商品名称
	 */
	private String name;
	
	/**
	 * 商品数量
	 */
	private Integer num;
	
	/**
	 * 商品广告
	 */
	private String adverst;
	
	/**
	 * 商品图片链接
	 */
	private String imgUrl;
	
	/**
	 * 进货价
	 */
	private BigDecimal inprice;
	
	/**
	 * 售卖价
	 */
	private BigDecimal outprice;
	
	/**
	 * 是否有效
	 */
	private boolean yn;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdverst() {
		return adverst;
	}

	public void setAdverst(String adverst) {
		this.adverst = adverst;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public BigDecimal getInprice() {
		return inprice;
	}

	public void setInprice(BigDecimal inprice) {
		this.inprice = inprice;
	}

	public BigDecimal getOutprice() {
		return outprice;
	}

	public void setOutprice(BigDecimal outprice) {
		this.outprice = outprice;
	}

	public boolean isYn() {
		return yn;
	}

	public void setYn(boolean yn) {
		this.yn = yn;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

}
