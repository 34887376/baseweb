package com.ms.redis.util;

public class ZookeeperConfig {
    private String zookeeperServers=null;
    private String zookeeperConfigRedisNodeName=null;
    private int zookeeperTimeout = 5000;

    public String getZookeeperServers() {
        return zookeeperServers;
    }

    public void setZookeeperServers(String zookeeperServers) {
        this.zookeeperServers = zookeeperServers;
    }

    public String getZookeeperConfigRedisNodeName() {
        return zookeeperConfigRedisNodeName;
    }

    public void setZookeeperConfigRedisNodeName(String zookeeperConfigRedisNodeName) {
        this.zookeeperConfigRedisNodeName = zookeeperConfigRedisNodeName;
    }

    public int getZookeeperTimeout() {
        return zookeeperTimeout;
    }

    public void setZookeeperTimeout(int zookeeperTimeout) {
        this.zookeeperTimeout = zookeeperTimeout;
    }
}
