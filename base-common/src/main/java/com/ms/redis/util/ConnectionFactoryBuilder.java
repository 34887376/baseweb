package com.ms.redis.util;

import redis.clients.jedis.JedisPoolConfig;

public class ConnectionFactoryBuilder {

    private ZookeeperConfig zkConfig = new ZookeeperConfig();
    private RedisClientConfig redisClientConfig = new RedisClientConfig();

    public int getErrorRetryTimes() {
        return redisClientConfig.getErrorRetryTimes();
    }

    public void setErrorRetryTimes(int errorRetryTimes) {
        this.redisClientConfig.setErrorRetryTimes(errorRetryTimes);
    }

    public RedisClientConfig getRedisClientConfig() {
        return redisClientConfig;
    }

    public void setRedisClientConfig(RedisClientConfig redisClientConfig) {
        this.redisClientConfig = redisClientConfig;
    }

    public String getZookeeperServers() {
        return zkConfig.getZookeeperServers();
    }

    public void setZookeeperServers(String zookeeperServers) {
        zkConfig.setZookeeperServers(zookeeperServers);
    }

    public String getZookeeperConfigRedisNodeName() {
        return zkConfig.getZookeeperConfigRedisNodeName();
    }

    public void setZookeeperConfigRedisNodeName(String zookeeperConfigRedisNodeName) {
        zkConfig.setZookeeperConfigRedisNodeName(zookeeperConfigRedisNodeName);
    }

    public void setMaxActive(int maxActive) {
        this.redisClientConfig.setMaxActive(maxActive);
    }

    public void setMaxIdle(int maxIdle) {
        this.redisClientConfig.setMaxIdle(maxIdle);
    }

    public void setMaxWait(int maxWait) {
        this.redisClientConfig.setMaxWait(maxWait);
    }

    public void setTestOnBorrow(boolean flag) {
        this.redisClientConfig.setTestOnBorrow(flag);
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return redisClientConfig.getJedisPoolConfig();
    }

    public void setTimeout(int timeout) {
        redisClientConfig.setTimeout(timeout);
    }

    public int getTimeout() {
        return redisClientConfig.getTimeout();
    }

    public int getZookeeperTimeout() {
        return zkConfig.getZookeeperTimeout();
    }

    public void setZookeeperTimeout(int zookeeperTimeout) {
        zkConfig.setZookeeperTimeout(zookeeperTimeout);
    }

    public String getMasterConfString() {
        return redisClientConfig.getMasterConfString();
    }

    public void setMasterConfString(String masterConfString) {
        redisClientConfig.setMasterConfString(masterConfString);
    }

    public String getSlaveConfString() {
        return redisClientConfig.getSlaveConfString();
    }

    public void setSlaveConfString(String slaveConfString) {
        redisClientConfig.setSlaveConfString(slaveConfString);
    }
}
