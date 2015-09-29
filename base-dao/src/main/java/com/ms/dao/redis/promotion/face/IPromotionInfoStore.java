package com.ms.dao.redis.promotion.face;

public interface IPromotionInfoStore {
	
	boolean setValue(String key,String value,long time);

}
