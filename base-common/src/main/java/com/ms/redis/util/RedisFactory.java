package com.ms.redis.util;

import org.springframework.beans.factory.FactoryBean;
import redis.clients.jedis.JedisPoolConfig;
import java.util.List;
public class RedisFactory implements FactoryBean{

    private ConnectionFactoryBuilder connectionFactoryBuilder = new ConnectionFactoryBuilder();
    private List<String> masterConfList = null;
    private List<String> slaveConfList = null;

    public Object getObject() throws Exception {
        //优先zookeeper配置，先检查
        if(connectionFactoryBuilder.getZookeeperServers()!=null && connectionFactoryBuilder.getZookeeperServers().trim().length()>0
                && connectionFactoryBuilder.getZookeeperConfigRedisNodeName()!=null && connectionFactoryBuilder.getZookeeperConfigRedisNodeName().trim().length()>0){
            return new RedisRedirctUtils(connectionFactoryBuilder);
        }
        //检查spring redis server配置
        else if (slaveConfList==null || slaveConfList.size()==0){
            return new RedisRedirctUtils(connectionFactoryBuilder,masterConfList);
        }else if (masterConfList!=null && masterConfList.size()>0 && slaveConfList!=null && slaveConfList.size()>0){
            return new RedisRedirctUtils(connectionFactoryBuilder,masterConfList,slaveConfList);
        }else{
            throw new ExceptionInInitializerError("redisUtils all init parameter is empty,please check spring config file!");
        }
    }

    public Class getObjectType() {
        return RedisRedirctUtils.class;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isSingleton() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setMasterConfList(List<String> masterConfList) {
        this.masterConfList = masterConfList;
    }

    public void setSlaveConfList(List<String> slaveConfList) {
        this.slaveConfList = slaveConfList;
    }

    public void setMasterConfString(String string){
        this.connectionFactoryBuilder.setMasterConfString(string);
    }

    public void setSlaveConfString(String string){
        this.connectionFactoryBuilder.setSlaveConfString(string);
    }

    public void setMaxActive(int maxActive) {
        this.connectionFactoryBuilder.setMaxActive(maxActive);
    }

    public void setMaxIdle(int maxIdle) {
        this.connectionFactoryBuilder.setMaxIdle(maxIdle);
    }

    public void setMaxWait(int maxWait) {
        this.connectionFactoryBuilder.setMaxWait(maxWait);
    }

    public void setTestOnBorrow(boolean flag) {
        this.connectionFactoryBuilder.setTestOnBorrow(flag);
    }

    public void setTimeout(int timeout) {
        this.connectionFactoryBuilder.setTimeout(timeout);
    }

    public void setZooKeeperTimeout(int timeout){
        this.connectionFactoryBuilder.setZookeeperTimeout(timeout);
    }

    public void setZooKeeperServers(String servers){
        this.connectionFactoryBuilder.setZookeeperServers(servers);
    }

    public void setZooKeeperConfigRedisNodeName(String path){
        this.connectionFactoryBuilder.setZookeeperConfigRedisNodeName(path);
    }

    public int getErrorRetryTimes() {
        return connectionFactoryBuilder.getErrorRetryTimes();
    }

    public void setErrorRetryTimes(int errorRetryTimes) {
        this.connectionFactoryBuilder.setErrorRetryTimes(errorRetryTimes);
    }

}

