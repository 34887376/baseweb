package com.ms.dao.promotion.redis;

import java.util.List;

public interface IPromotionRedis {

	/**
	 * 存放值到redis中，没有过期时间
	 * @param key
	 * @param value
	 * @return
	 */
	boolean setValue(String key,String value);
	
	/**
	 * 存放值到redis中，过期时间为expriedTime，单位为秒
	 * @param key
	 * @param value
	 * @param expriedTime
	 * @return
	 */
	boolean setValue(String key,String value,long expriedTime);
	
	/**
	 * 删除redis中对应的key
	 * @param key
	 * @return
	 */
	long delValue(String key);
	
	/**
	 * 根据key值获取redis中的值
	 * @param key
	 * @return
	 */
	String getValue(String key);
	
	/**
	 * 根据keys批量获取redis中的值
	 * @param keys
	 * @return
	 */
	List<String> getValuesByKeyList(List<String> keys);

}
