package com.ms.domain.promotionsequence.dao;

import java.io.Serializable;
import java.util.Date;

public class PromotionSequenceDAO implements Serializable {
	
	private static final long serialVersionUID = 1870674956667450428L;

	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 促销id
	 */
	private Long promotionId;
	
	/**
	 * 是否被加载过
	 */
	private Integer hasLoad;
	
	/**
	 * 下一个序号，指的是id
	 */
	private Long nextOrder;
	
	/**
	 * 促销开始时间
	 */
	private Date startTime;
	
	/**
	 * 促销结束时间
	 */
	private Date endTime;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Integer getHasLoad() {
		return hasLoad;
	}

	public void setHasLoad(Integer hasLoad) {
		this.hasLoad = hasLoad;
	}

	public Long getNextOrder() {
		return nextOrder;
	}

	public void setNextOrder(Long nextOrder) {
		this.nextOrder = nextOrder;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
