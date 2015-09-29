package com.ms.dao.promotion.redis.impl;

import java.util.List;

import com.ms.dao.base.dao.AbstractBaseRedisDao;
import com.ms.dao.promotion.redis.IPromotionRedis;

public class PromotionRedisImpl extends AbstractBaseRedisDao implements IPromotionRedis {

	public boolean setValue(String key, String value) {
		try{
			return this.set(key, value);
		}catch(Exception e){
			return false;
		}
	}

	public boolean setValue(String key, String value, long expriedTime) {
		try{
			return this.set(key, value, expriedTime);
		}catch(Exception e){
			return false;
		}
	}

	public long delValue(String key) {
		try{
			return this.del(key);
		}catch(Exception e){
			return -1L;
		}
	}

	public String getValue(String key) {
		try{
			return this.getValue(key);
		}catch(Exception e){
			return null;
		}
	}
	
	public List<String> getValuesByKeyList(List<String> keys){
		try{
			return this.getValuesByKeyList(keys);
		}catch(Exception e){
			return null;
		}
	}

}
