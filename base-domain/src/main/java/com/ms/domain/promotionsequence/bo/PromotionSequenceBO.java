package com.ms.domain.promotionsequence.bo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.ms.domain.business.constant.PromotionStatusConstant;

public class PromotionSequenceBO implements Serializable {
	
	private static final long serialVersionUID = -7370610437652444498L;

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
	 * 上一个序号，指的是id
	 */
	private Long previosOrder;
	
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
	
	/**
	 * 促销的状态
	 */
	private Integer status;
	
	/**
	 * 是否有效
	 */
	private Integer yn;
	
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

	public Long getPreviosOrder() {
		return previosOrder;
	}

	public void setPreviosOrder(Long previosOrder) {
		this.previosOrder = previosOrder;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getStatus() {
		Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startTime);
//        int startYear = startCalendar.get(Calendar.YEAR);
//        int startMonth = startCalendar.get(Calendar.MONTH);
//        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
//        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
//        int startMinute = startCalendar.get(Calendar.MINUTE);
        
		Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endTime);
//        int endYear = endCalendar.get(Calendar.YEAR);
//        int endMonth = endCalendar.get(Calendar.MONTH);
//        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
//        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
//        int endMinute = endCalendar.get(Calendar.MINUTE);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
//        int nowYear = nowCalendar.get(Calendar.YEAR);
//        int nowMonth = nowCalendar.get(Calendar.MONTH);
//        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
//        int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
//        int nowMinute = nowCalendar.get(Calendar.MINUTE);
        

        //未开始的
        if(startCalendar.before(nowCalendar)){
        	long distinctTime = startCalendar.getTimeInMillis() - nowCalendar.getTimeInMillis();
        	long waitStart = 10 * 60 * 1000;
        	if( distinctTime > waitStart){
        		return PromotionStatusConstant.WAIT_PROMOTION;
        	}else{
        		return PromotionStatusConstant.IMMEDIATE_PROMOTION;
        	}
        }
        
        //正在进行的
        if(startCalendar.after(nowCalendar) && nowCalendar.before(endCalendar)){
        	return PromotionStatusConstant.START_PROMOTION;
        }
        
        //结束的
        if(nowCalendar.after(endCalendar)){
        	return PromotionStatusConstant.LOSS_PROMOTION;
        }
        return PromotionStatusConstant.WAIT_PROMOTION;
	}


}
