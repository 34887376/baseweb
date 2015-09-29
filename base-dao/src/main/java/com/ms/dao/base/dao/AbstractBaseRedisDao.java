package com.ms.dao.base.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public abstract class AbstractBaseRedisDao<K, V> {

	@Autowired
	protected RedisTemplate<K, V> redisTemplate;

	/**
	 * 添加一个有超时时间的key-value值
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	public boolean set(final String key, final String value, final long time) {

		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] redisKey = serializer.serialize(key);
				byte[] name = serializer.serialize(value);
				connection.setEx(redisKey, time, name);
				return true;
			}
		});
		return result;
	}
	
	/**
	 * 添加一个没有超时时间的key-value值
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, final String value) {

		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] redisKey = serializer.serialize(key);
				byte[] name = serializer.serialize(value);
				connection.set(redisKey, name);
				return true;
			}
		});
		return result;
	}
	
	/**
	 * 获取redis中的值
	 * @param key
	 * @return
	 */
	public String get(final String key){
		
		String result = redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] redisKey = serializer.serialize(key);
				byte[] redisValue = connection.get(redisKey);
				String value = serializer.deserialize(redisValue);
				return value;
			}
		});
		return result;
	}
	
	/**
	 * 删除redis中的key值
	 * @param key
	 * @return
	 */
	public Long del(final String key) {

		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] redisKey = serializer.serialize(key);
				Long delNum = connection.del(redisKey);
				return delNum;
			}
		});
		return result;
	}
	
	
	/**
	 * 判断redis中是否存在一个值
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {

		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] redisKey = serializer.serialize(key);
				return connection.exists(redisKey);
			}
		});
		return result;
	}
	
	
	/**
	 * 添加值到key中，value为set集合;返回添加成功的个数
	 * @param key
	 * @param value
	 * @return
	 */
	public long sAdd(final String key, final String value) {

		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] redisKey = serializer.serialize(key);
				byte[] name = serializer.serialize(value);
				long effectNum = connection.sAdd(redisKey, name);
				return effectNum;
			}
		});
		return result;
	}
	
	/**
	 * 根据key列表，从redis中批量获取多个值
	 * @param keys
	 * @return
	 */
	public List<String> getValuesByKeys(final List<String> keys) {

		List<String> result = redisTemplate.execute(new RedisCallback<List<String>>() {
			public List<String> doInRedis(RedisConnection connection)
					throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				List<byte[]> keyList = new ArrayList<byte[]>();
				int maxLength=10;
				for(String key : keys){
					if(key.length() > maxLength){
						maxLength = key.length();
					}
					byte[] redisKey = serializer.serialize(key);
					keyList.add(redisKey);
				}
				byte[][] keyArray = new byte[maxLength][keyList.size()];
				List<byte[]> allRecords = connection.mGet(keyList.toArray(keyArray));
				
				List<String> valueList = new ArrayList<String>();
				if(CollectionUtils.isNotEmpty(allRecords)){
					for(byte[] value : allRecords){
						valueList.add(serializer.deserialize(value));
					}
				}
				return valueList;
			}
		});
		return result;
	}
	
	
	

	/**
	 * 设置redisTemplate
	 * 
	 * @param redisTemplate
	 *            the redisTemplate to set
	 */
	public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 获取 RedisSerializer <br>
	 * ------------------------------<br>
	 */
	protected RedisSerializer<String> getRedisSerializer() {
		return redisTemplate.getStringSerializer();
	}
}
