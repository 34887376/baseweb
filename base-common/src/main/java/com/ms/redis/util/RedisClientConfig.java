package com.ms.redis.util;

import redis.clients.jedis.JedisPoolConfig;

public class RedisClientConfig {
    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    private int timeout = 2000;
    //默认重试次数为0
    private int errorRetryTimes = 0;
    private String masterConfString=null;
    private String slaveConfString=null;

    public int getErrorRetryTimes() {
        return errorRetryTimes;
    }

    public void setErrorRetryTimes(int retryTimes) {
        this.errorRetryTimes = retryTimes;
    }

    public void setMaxActive(int maxActive) {
        this.jedisPoolConfig.setMaxTotal(maxActive);
    }

    public void setMaxIdle(int maxIdle) {
        this.jedisPoolConfig.setMaxIdle(maxIdle);
    }

    public void setMaxWait(int maxWait) {
        this.jedisPoolConfig.setMaxWaitMillis(maxWait);
    }

    public void setTestOnBorrow(boolean flag) {
        this.jedisPoolConfig.setTestOnBorrow(flag);
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getMasterConfString() {
        return masterConfString;
    }

    public void setMasterConfString(String masterConfString) {
        this.masterConfString = masterConfString;
    }

    public String getSlaveConfString() {
        return slaveConfString;
    }

    public void setSlaveConfString(String slaveConfString) {
        this.slaveConfString = slaveConfString;
    }
}

