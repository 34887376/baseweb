package com.ms.dao.redis.promotion.face.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.ms.dao.base.dao.AbstractBaseRedisDao;
import com.ms.dao.redis.promotion.face.IPromotionInfoStore;

public class PromotionInfoStoreImpl extends AbstractBaseRedisDao<String,String> implements IPromotionInfoStore {

	public boolean setValue(final String key, final String value, long time) {
		
		 boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
	            public Boolean doInRedis(RedisConnection connection)  
	                    throws DataAccessException {
	                RedisSerializer<String> serializer = getRedisSerializer();  
	                byte[] redisKey = serializer.serialize(key);  
	                byte[] name = serializer.serialize(value);  
	                connection.setEx(redisKey, 1000, name);
	                return true;
	            }  
	        });
	        return result;  
	}

}
