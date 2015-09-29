package com.ms.redis.util;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;
import redis.clients.util.SafeEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Logger;

import base.test.base.util.JsonUtil;
import base.test.base.util.SerializaObjUtils;
import redis.clients.util.Slowlog;

public class RedisRedirctUtils implements ZkDataListener {

	private RedisRedirctUtils() {
		super();
	}
	

    private static Logger log = Logger.getLogger(RedisRedirctUtils.class);
    private ShardedJedisPool writePool = null;
    private ShardedJedisPool readPool = null;
    private int errorRetryTimes = 0;
    private ConnectionFactoryBuilder connectionFactoryBuilder = null;
    private List<String> masterConfList = null;
    private List<String> slaveConfList = null;
    private ZookeeperClient zkClient = null;

    /**
     * 生成分片实例
     *
     * @param connectionFactoryBuilder redis client pool 初始化信息
     * @param masterConfList           master分片配置
     */
    public RedisRedirctUtils(ConnectionFactoryBuilder connectionFactoryBuilder, List<String> masterConfList) {
        this(connectionFactoryBuilder, masterConfList, null);
    }

    /**
     * 生成master slave主从实例
     *
     * @param connectionFactoryBuilder redis client pool 初始化信息
     * @param masterConfList           master分片信息
     * @param slaveConfList            slave分片信息
     */
    public RedisRedirctUtils(ConnectionFactoryBuilder connectionFactoryBuilder, List<String> masterConfList, List<String> slaveConfList) {
        this.connectionFactoryBuilder = connectionFactoryBuilder;
        this.masterConfList = masterConfList;
        this.slaveConfList = slaveConfList;
        init();
    }

    /**
     * 生成实例
     *
     * @since 1.0.3
     */
    public RedisRedirctUtils(ConnectionFactoryBuilder connectionFactoryBuilder) {
        //检查是否是zookeeper 配置
        if (StringUtils.hasLength(connectionFactoryBuilder.getZookeeperServers())
                && StringUtils.hasLength(connectionFactoryBuilder.getZookeeperConfigRedisNodeName())) {
            try {
                //初始化zookeeper client
                zkClient = new ZookeeperClient(connectionFactoryBuilder.getZookeeperServers(), connectionFactoryBuilder.getZookeeperTimeout());
                //注册节点与开启侦听服务
                zkClient.subscribeDataChanges(connectionFactoryBuilder.getZookeeperConfigRedisNodeName(), this);
                byte[] data = zkClient.readData(connectionFactoryBuilder.getZookeeperConfigRedisNodeName());
                if (data != null && data.length > 0) {
                    init(data);
                }
            } catch (IOException e) {
                log.error(e);
            }
        } else {
            this.connectionFactoryBuilder = connectionFactoryBuilder;
            init();
        }
    }

    private void init() {
        log.info("init start~");
        List<JedisShardInfo> wShards = null;
        List<JedisShardInfo> rShards = null;
        //检查masterConfString 是否设置
        if (StringUtils.hasLength(connectionFactoryBuilder.getMasterConfString())) {
            //log.info("MasterConfString:" + connectionFactoryBuilder.getMasterConfString());
            masterConfList = Arrays.asList(connectionFactoryBuilder.getMasterConfString().split("(?:\\s|,)+"));
        }
        if (CollectionUtils.isEmpty(this.masterConfList)) {
            throw new ExceptionInInitializerError("masterConfString is empty！");
        }
        wShards = new ArrayList<JedisShardInfo>();
        for (String wAddr : this.masterConfList) {
            if (wAddr != null) {
                String[] wAddrArray = wAddr.split(":");
                if (wAddrArray.length == 1) {
                    throw new ExceptionInInitializerError(wAddr + " is not include host:port or host:port:passwd after split \":\"");
                }
                String host = wAddrArray[0];
                int port = Integer.valueOf(wAddrArray[1]);
                JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, connectionFactoryBuilder.getTimeout());
                log.info("masterConfList:" + jedisShardInfo.toString());
                //检查密码是否需要设置
                if (wAddrArray.length == 3 && StringUtils.hasLength(wAddrArray[2])) {
                    jedisShardInfo.setPassword(wAddrArray[2]);
                }
                wShards.add(jedisShardInfo);
            }
        }
        this.writePool = new ShardedJedisPool(connectionFactoryBuilder.getJedisPoolConfig(), wShards);

        //检查slaveConfString 是否设置,并且检查主串与从串是否一致
        if (StringUtils.hasLength(connectionFactoryBuilder.getSlaveConfString()) &&
                !connectionFactoryBuilder.getSlaveConfString().equals(connectionFactoryBuilder.getMasterConfString())) {
            //log.info("SlaveConfString:" + connectionFactoryBuilder.getSlaveConfString());
            slaveConfList = Arrays.asList(connectionFactoryBuilder.getSlaveConfString().split("(?:\\s|,)+"));
            //检查是否有slave配置
            if (!CollectionUtils.isEmpty(this.slaveConfList)) {
                rShards = new ArrayList<JedisShardInfo>();
                for (String rAddr : this.slaveConfList) {
                    if (rAddr != null) {
                        String[] rAddrArray = rAddr.split(":");
                        if (rAddrArray.length == 1) {
                            throw new ExceptionInInitializerError(rAddr + " is not include host:port or host:port:passwd after split \":\"");
                        }
                        String host = rAddrArray[0];
                        int port = Integer.valueOf(rAddrArray[1]);
                        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, connectionFactoryBuilder.getTimeout());
                        //检查密码是否需要设置
                        if (rAddrArray.length == 3 && StringUtils.hasLength(rAddrArray[2])) {
                            jedisShardInfo.setPassword(rAddrArray[2]);
                        }
                        log.info("slaveConfList:" + jedisShardInfo.toString());
                        rShards.add(jedisShardInfo);
                    }
                }
                this.readPool = new ShardedJedisPool(connectionFactoryBuilder.getJedisPoolConfig(), rShards);
                //在开启从机时，错误次数默认为1
                this.errorRetryTimes = 1;
            }
        }

        //出错后的重试次数
        if (connectionFactoryBuilder.getErrorRetryTimes() > 0) {
            this.errorRetryTimes = connectionFactoryBuilder.getErrorRetryTimes();
            log.error("after error occured redis api retry times is " + this.errorRetryTimes);
        }

        //是否有错误重试检查
        if (connectionFactoryBuilder.getErrorRetryTimes() > 0 && readPool == null) {
            //将主的连接池与从连接池设置为相同，为重试做准备
            this.readPool = this.writePool;
            log.error("readPool is null and errorRetryTimes >0，readPool is set to writePool");
        }

        //Object转码类定义
        log.info("init end~");
    }

    //通过 zookeeper json数据初始化
    private void init(Object configData) {
        if (configData != null) {
            if (log.isDebugEnabled()) {
                log.debug(new String((byte[]) configData));
            }
            if (connectionFactoryBuilder == null) {
                connectionFactoryBuilder = new ConnectionFactoryBuilder();
            }
        } else {
            log.error("init configData is null~");
            return;
        }
        byte[] decompressData = null;
        //进行中zkconfig.360buy.com数据解压缩分析
        try {
            decompressData = SerializaObjUtils.decompress((byte[]) configData);
        } catch (Exception ex) {
            log.error("decompress occured " + ex.getMessage() + ",try plain-text init builder!");
        }
        try {
            if (decompressData != null) {
                ZookeeperDomain zkDomain = JsonUtil.readValue(new ByteArrayInputStream(decompressData), ZookeeperDomain.class);
                log.debug(zkDomain.getData());
                log.debug(zkDomain.getZother());
                if (StringUtils.hasLength(zkDomain.getData().get("masterConfString"))) {
                    this.connectionFactoryBuilder.setMasterConfString(zkDomain.getData().get("masterConfString"));
                } else {
                    log.fatal("zookeeper path " + connectionFactoryBuilder.getZookeeperConfigRedisNodeName() + " masterConfString is null");
                    return;
                }
                if (StringUtils.hasLength(zkDomain.getData().get("slaveConfString"))) {
                    this.connectionFactoryBuilder.setSlaveConfString(zkDomain.getData().get("slaveConfString"));
                }
                if (StringUtils.hasLength(zkDomain.getData().get("maxActive"))) {
                    this.connectionFactoryBuilder.setMaxActive(Integer.parseInt(zkDomain.getData().get("maxActive")));
                }
                if (StringUtils.hasLength(zkDomain.getData().get("maxIdle"))) {
                    this.connectionFactoryBuilder.setMaxIdle(Integer.parseInt(zkDomain.getData().get("maxIdle")));
                }
                if (StringUtils.hasLength(zkDomain.getData().get("maxWait"))) {
                    this.connectionFactoryBuilder.setMaxWait(Integer.parseInt(zkDomain.getData().get("maxWait")));
                }
                if (StringUtils.hasLength(zkDomain.getData().get("timeout"))) {
                    this.connectionFactoryBuilder.setTimeout(Integer.parseInt(zkDomain.getData().get("timeout")));
                }
                //从集群存在的情况下，可以用错误重试
                if (StringUtils.hasLength(zkDomain.getData().get("errorRetryTimes"))) {
                    this.connectionFactoryBuilder.setErrorRetryTimes(Integer.parseInt(zkDomain.getData().get("errorRetryTimes")));
                }
            } else {
                //分析出错后，再当成存文本分析
                RedisClientConfig redisClientConfig = JsonUtil.readValue(new ByteArrayInputStream((byte[]) configData), RedisClientConfig.class);
                connectionFactoryBuilder.setRedisClientConfig(redisClientConfig);
            }
            //初始化 redis client
            synchronized (this) {
                //销毁之前的连接池
                try{
                    this.destroy();
                }catch(Exception ex){
                    log.error("init() destroy connection pool error!"+ex.getMessage());
                }
                //开始初始化
                init();
            }
        } catch (Exception ex) {
            log.fatal("redis init error:" + ex);
        }
    }

    public String getShardInfo(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.getShardInfo(key).toString();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public List<Slowlog> getSlowLog(long slowLogRows) throws Exception {
        boolean flag = true;
        List<Slowlog> result = new ArrayList<Slowlog>();
        ShardedJedis master = null;
        ShardedJedis slave = null;
        SortedMap<String, String> re = null;
        try {
            // ShardedJedisPool[] pools = new ShardedJedisPool[]{writePool,readPool};
            master = writePool.getResource();
            for (Jedis jedis : master.getAllShards()) {
                result.addAll(jedis.slowlogGet(10));
            }
            if (readPool != null) {
                slave = readPool.getResource();
                for (Jedis jedis : slave.getAllShards()) {
                    result.addAll(jedis.slowlogGet(10));
                }
            }
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(master);
            if (slave != null) {
                readPool.returnBrokenResource(slave);
            }
            throw new Exception(ex);
        } finally {
            if (flag) {
                writePool.returnResource(master);
                if (slave != null) {
                    readPool.returnResource(slave);
                }
            }
        }
        return result;
    }

    public Long del(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.del(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public byte[] get(byte[] key) throws Exception {
        return get(errorRetryTimes, key);
    }

    private byte[] get(int toTryCount, byte[] key) throws Exception {
        byte[] result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.get(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = get(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Boolean exists(byte[] key) throws Exception {
        Boolean result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            j = writePool.getResource();
            result = j.exists(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String type(byte[] key) throws Exception {
        String result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            j = writePool.getResource();
            result = j.type(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long expire(byte[] key, int seconds) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.expire(key, seconds);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long expireAt(byte[] key, long unixTime) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.expireAt(key, unixTime);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long ttl(byte[] key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.ttl(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public byte[] getSet(byte[] key, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        byte[] result = null;
        try {
            j = writePool.getResource();
            result = j.getSet(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long setnx(byte[] key, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.setnx(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String setex(byte[] key, int seconds, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.setex(key, seconds, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long decrBy(byte[] key, long integer) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.decrBy(key, integer);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long decr(byte[] key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.decr(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long incrBy(byte[] key, long integer) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.incrBy(key, integer);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long incr(byte[] key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.incr(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long append(byte[] key, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.append(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public byte[] substr(byte[] key, int start, int end) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        byte[] result = null;
        try {
            j = writePool.getResource();
            result = j.substr(key, start, end);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long hset(byte[] key, byte[] field, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hset(key, field, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public byte[] hget(byte[] key, byte[] field) throws Exception {
        return hget(errorRetryTimes, key, field);
    }

    private byte[] hget(int toTryCount, byte[] key, byte[] field) throws Exception {
        byte[] result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hget(key, field);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hget(toTryCount - 1, key, field);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Long hsetnx(byte[] key, byte[] field, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hsetnx(key, field, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String hmset(byte[] key, Map<byte[], byte[]> hash) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.hmset(key, hash);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public List<byte[]> hmget(byte[] key, byte[]... fields) throws Exception {
        return hmget(errorRetryTimes, key, fields);
    }

    private List<byte[]> hmget(int toTryCount, byte[] key, byte[]... fields) throws Exception {
        List<byte[]> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hmget(key, fields);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hmget(toTryCount - 1, key, fields);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Long hincrBy(byte[] key, byte[] field, long value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hincrBy(key, field, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Boolean hexists(byte[] key, byte[] field) throws Exception {
        Boolean result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            j = writePool.getResource();
            result = j.hexists(key, field);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long hdel(byte[] key, byte[] field) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hdel(key, field);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String lset(byte[] key, int index, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.lset(key, index, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long lrem(byte[] key, int count, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.lrem(key, count, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public byte[] lpop(byte[] key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        byte[] result = null;
        try {
            j = writePool.getResource();
            result = j.lpop(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public byte[] rpop(byte[] key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        byte[] result = null;
        try {
            j = writePool.getResource();
            result = j.rpop(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long sadd(byte[] key, byte[]... member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.sadd(key, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long srem(byte[] key, byte[]... member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.srem(key, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public byte[] spop(byte[] key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        byte[] result = null;
        try {
            j = writePool.getResource();
            result = j.spop(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long zadd(byte[] key, double score, byte[] member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.zadd(key, score, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long zrem(byte[] key, byte[]... member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.zrem(key, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public Double zincrby(byte[] key, double score, byte[] member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        double result = -1;
        try {
            j = writePool.getResource();
            result = j.zincrby(key, score, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String get(String key) throws Exception {
        return get(errorRetryTimes, key);
    }

    private String get(int toTryCount, String key) throws Exception {
        String result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.get(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = get(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 兼容非分片下使用的mget命令,如果某个key不存在，针对不存在的key返回null,按keys请考虑网络流量和redis server处理性能谨慎使用~
     *
     * @param keys String... Array
     * @return 按keys顺序返回get结果 List<String>
     * @since 1.0.2
     */
    public List<String> mget(String... keys) throws Exception {
        Map<String, String> hashMapKv = mget(errorRetryTimes, keys);
        if (hashMapKv == null || hashMapKv.size() == 0) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<String>(keys.length);
        //按keys 顺序，保存mget结果
        for (String key : keys) {
            result.add(hashMapKv.get(key));
        }
        return result;
    }

    /**
     * 兼容非分片下使用的mget命令,如果某个key不存在，针对不存在的key返回null,按keys请考虑网络流量和redis server处理性能谨慎使用~
     * 实现方式和mget一样，只是返回结果改为Map
     * 注意：不保证入参keys.length和返回的map.size()数量一致
     *
     * @param keys String... Array
     * @return 返回get结果 Map<String,String>
     * @since 1.0.9-SNAPSHOT
     */
    public Map<String, String> mget2map(String... keys) throws Exception {
        return mget(errorRetryTimes, keys);
    }

    private Map<String, String> mget(int toTryCount, String... keys) throws Exception {
        if (keys == null || keys.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        HashMap<Jedis, List<String>> hashMapJedisGroup = new HashMap<Jedis, List<String>>(keys.length);
        HashMap<String, String> hashMapKv = new HashMap<String, String>(keys.length);
        List<String> listKeys = null;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            //查出key对应的shard
            for (int i = 0; i < keys.length; i++) {
                Jedis jedisChild = j.getShard(keys[i]);
                if (hashMapJedisGroup.containsKey(jedisChild)) {
                    hashMapJedisGroup.get(jedisChild).add(keys[i]);
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(keys[i]);
                    hashMapJedisGroup.put(jedisChild, list);
                }
            }
            //按shard分别执行mget
            int i;
            for (Jedis jedisChild : hashMapJedisGroup.keySet()) {
                listKeys = hashMapJedisGroup.get(jedisChild);
                i = 0;
                for (String str : jedisChild.mget(listKeys.toArray(new String[listKeys.size()]))) {
                    hashMapKv.put(listKeys.get(i), str);
                    i++;
                }
            }
            return hashMapKv;
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = mget(toTryCount - 1, keys);
            } else {
                writePool.returnBrokenResource(j);
                String redisAddr = null;
                if (!CollectionUtils.isEmpty(listKeys)) {
                    redisAddr = j.getShardInfo(listKeys.get(0)).toString();
                }
                throw new Exception(ex + "," + redisAddr);
            }
        } finally {
            hashMapJedisGroup = null;
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return result;
    }


    /**
     * 通过管道批处理执行lrange(key,0,-1)，如果某个key不存在，针对此key返回空list,请考虑网络流量和redis server处理性能谨慎使用~
     * 实现方式和lrange2pipeline一样，只是返回结果改为Map
     * 注意：不保证入参keys.length和返回的map.size()的数量一致
     *
     * @param keys String... Array
     * @return 返回lrange结果 Map<String,List<String>>
     * @since 1.0.9-SNAPSHOT
     */
    public Map<String, List<String>> lrange2pipeline2map(String... keys) throws Exception {
        return lrange2pipeline(errorRetryTimes, keys);
    }

    /**
     * 通过管道批处理执行lrange(key,0,-1)，如果某个key不存在，针对此key返回空list,请考虑网络流量和redis server处理性能谨慎使用~
     *
     * @param keys String... Array
     * @return 按keys顺序返回lrange结果 List<List<String>>
     * @since 1.0.5
     */
    public List<List<String>> lrange2pipeline(String... keys) throws Exception {
        Map<String, List<String>> hashMapKv = lrange2pipeline(errorRetryTimes, keys);
        if (hashMapKv == null || hashMapKv.size() == 0) {
            return Collections.emptyList();
        }
        List<List<String>> result = new ArrayList<List<String>>(keys.length);
        //按keys 顺序，保存mget结果
        for (String key : keys) {
            result.add(hashMapKv.get(key));
        }
        return result;
    }

    private Map<String, List<String>> lrange2pipeline(int toTryCount, String... keys) throws Exception {
        if (keys == null || keys.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        HashMap<Jedis, List<String>> hashMapJedisGroup = new HashMap<Jedis, List<String>>(keys.length);
        HashMap<String, List<String>> hashMapKv = new HashMap<String, List<String>>(keys.length);
        List<String> listKeys = null;
        Pipeline p;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            //查出key对出的shard
            for (int i = 0; i < keys.length; i++) {
                Jedis jedisChild = j.getShard(keys[i]);
                if (hashMapJedisGroup.containsKey(jedisChild)) {
                    hashMapJedisGroup.get(jedisChild).add(keys[i]);
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(keys[i]);
                    hashMapJedisGroup.put(jedisChild, list);
                }
            }
            //按shard分别执行命令
            int i;
            for (Jedis jedisChild : hashMapJedisGroup.keySet()) {
                listKeys = hashMapJedisGroup.get(jedisChild);
                p = jedisChild.pipelined();

                for (String key : listKeys) {
                    p.lrange(key, 0, -1);
                }
                i = 0;
                for (Object obj : p.syncAndReturnAll()) {
                    //TODO 此处为强转型，需要注意jedis客户端升级后带来的影响
                    hashMapKv.put(listKeys.get(i), (List<String>) obj);
                    i++;
                }
            }
            return hashMapKv;
            /*result = new ArrayList<List<String>>(keys.length);
            //按keys 顺序，保存命令结果
            for (String key : keys) {
                result.add(hashMapKv.get(key));
            }*/
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = lrange2pipeline(toTryCount - 1, keys);
            } else {
                writePool.returnBrokenResource(j);
                String redisAddr = null;
                if (!CollectionUtils.isEmpty(listKeys)) {
                    redisAddr = j.getShardInfo(listKeys.get(0)).toString();
                }
                throw new Exception(ex + "," + redisAddr);
            }
        } finally {
            hashMapJedisGroup = null;
            listKeys = null;
            p = null;
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return result;
    }


    /**
     * 通过管道批处理执行hmget(key,fields)，如果某个key不存在，针对此key返回空map,请考虑网络流量和redis server处理性能谨慎使用~
     *
     * @param keys   Set<String>
     * @param fields String...
     * @return Map<String,List<String>>
     * @since 1.0.7-SNAPSHOT
     */
    public Map<String, List<String>> hmget2pipeline(Set<String> keys, String[] fields) throws Exception {
        return hmget2pipeline(errorRetryTimes, keys, fields);
    }

    private Map<String, List<String>> hmget2pipeline(int toTryCount, Set<String> keys, String[] fields) throws Exception {
        if (keys == null || keys.size() == 0) {
            return Collections.emptyMap();
        }
        ShardedJedis j = null;
        boolean flag = true;
        HashMap<Jedis, List<String>> hashMapJedisGroup = new HashMap<Jedis, List<String>>(keys.size());
        Map<String, List<String>> hashMapKv = new HashMap<String, List<String>>(keys.size());
        List<String> listKeys = null;
        Pipeline p;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            //查出key对出的shard
            for (String key : keys) {
                Jedis jedisChild = j.getShard(key);
                if (hashMapJedisGroup.containsKey(jedisChild)) {
                    hashMapJedisGroup.get(jedisChild).add(key);
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(key);
                    hashMapJedisGroup.put(jedisChild, list);
                }
            }
            //按shard分别执行命令
            int i;
            for (Jedis jedisChild : hashMapJedisGroup.keySet()) {
                listKeys = hashMapJedisGroup.get(jedisChild);
                p = jedisChild.pipelined();
                for (String key : listKeys) {
                    p.hmget(key, fields);
                }
                i = 0;
                for (Object obj : p.syncAndReturnAll()) {
                    //TODO 此处为强转型，需要注意jedis客户端升级后带来的影响
                    hashMapKv.put(listKeys.get(i), (List<String>) obj);
                    i++;
                }
            }
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                hashMapKv = hmget2pipeline(toTryCount - 1, keys, fields);
            } else {
                writePool.returnBrokenResource(j);
                String redisAddr = null;
                if (!CollectionUtils.isEmpty(listKeys)) {
                    redisAddr = j.getShardInfo(listKeys.get(0)).toString();
                }
                throw new Exception(ex + "," + redisAddr);
            }
        } finally {
            hashMapJedisGroup = null;
            listKeys = null;
            p = null;
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return hashMapKv;
    }

    /**
     * 通过管道批处理执行hgetAll(key)，如果某个key不存在，针对此key返回空map,请考虑网络流量和redis server处理性能谨慎使用~
     *
     * @param keys Set<String>
     * @return Map<String,Map<String,String>>
     * @since 1.0.10-SNAPSHOT
     */
    public Map<String, Map<String, String>> hgetAll2pipeline(Set<String> keys) throws Exception {
        return hgetAll2pipeline(errorRetryTimes, keys);
    }

    private Map<String, Map<String, String>> hgetAll2pipeline(int toTryCount, Set<String> keys) throws Exception {
        if (keys == null || keys.size() == 0) {
            return Collections.emptyMap();
        }
        ShardedJedis j = null;
        boolean flag = true;
        HashMap<Jedis, List<String>> hashMapJedisGroup = new HashMap<Jedis, List<String>>(keys.size());
        Map<String, Map<String, String>> hashMapKv = new HashMap<String, Map<String, String>>(keys.size());
        List<String> listKeys = null;
        Pipeline p;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            //查出key对出的shard
            for (String key : keys) {
                Jedis jedisChild = j.getShard(key);
                if (hashMapJedisGroup.containsKey(jedisChild)) {
                    hashMapJedisGroup.get(jedisChild).add(key);
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(key);
                    hashMapJedisGroup.put(jedisChild, list);
                }
            }
            //按shard分别执行命令
            int i;
            for (Jedis jedisChild : hashMapJedisGroup.keySet()) {
                listKeys = hashMapJedisGroup.get(jedisChild);
                p = jedisChild.pipelined();
                for (String key : listKeys) {
                    p.hgetAll(key);
                }
                i = 0;
                for (Object obj : p.syncAndReturnAll()) {
                    //TODO 此处为强转型，需要注意jedis客户端升级后带来的影响
                    hashMapKv.put(listKeys.get(i), (Map<String, String>) obj);
                    i++;
                }
            }
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                hashMapKv = hgetAll2pipeline(toTryCount - 1, keys);
            } else {
                writePool.returnBrokenResource(j);
                String redisAddr = null;
                if (!CollectionUtils.isEmpty(listKeys)) {
                    redisAddr = j.getShardInfo(listKeys.get(0)).toString();
                }
                throw new Exception(ex + "," + redisAddr);
            }
        } finally {
            hashMapJedisGroup = null;
            listKeys = null;
            p = null;
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return hashMapKv;
    }

    public Boolean exists(String key) throws Exception {
        Boolean result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            j = writePool.getResource();
            result = j.exists(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String type(String key) throws Exception {
        String result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            j = writePool.getResource();
            result = j.type(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long expire(String key, int seconds) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.expire(key, seconds);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long expireAt(String key, long unixTime) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.expireAt(key, unixTime);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long ttl(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.ttl(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public boolean setbit(String key, long offset, boolean value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Boolean result = null;
        try {
            j = writePool.getResource();
            result = j.setbit(key, offset, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public boolean getbit(String key, long offset) throws Exception {
        return getbit(errorRetryTimes, key, offset);
    }

    private boolean getbit(int toTryCount, String key, long offset) throws Exception {
        Boolean result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.getbit(key, offset);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = getbit(toTryCount - 1, key, offset);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public long setrange(String key, long offset, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.setrange(key, offset, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String getrange(String key, long startOffset, long endOffset) throws Exception {
        return getrange(errorRetryTimes, key, startOffset, endOffset);
    }

    private String getrange(int toTryCount, String key, long startOffset, long endOffset) throws Exception {
        String result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.getrange(key, startOffset, endOffset);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = getrange(toTryCount - 1, key, startOffset, endOffset);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public String getSet(String key, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.getSet(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long setnx(String key, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.setnx(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 通过管道，执行setnx和expire两个命令
     *
     * @param key
     * @param seconds 过期时间，单位s
     * @param value
     * @return List<Ojbect> 返回setnx 和 expire两个命令的结果合集
     */
    public List<Object> setnx(String key, int seconds, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        List<Object> result = null;
        Pipeline p = null;
        try {
            j = writePool.getResource();
            p = j.getShard(key).pipelined();
            p.setnx(key, value);
            p.expire(key, seconds);
            result = p.syncAndReturnAll();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
            p = null;
        }
        return result;

    }

    @Deprecated
    public List<Object> setnx(String key, String value, int seconds) throws Exception {
        return setnx(key, seconds, value);
    }

    /**
     * 通过管道，执行set和expire两个命令,对象序列化是通过字节流实现，并且强制gzip压缩,效率低于set方法，而且会增加redis内存占用率,
     * 只推荐domain对象用此方法，获取对象请用getObject(key)
     *
     * @param key
     * @param value
     * @return List<Ojbect> 返回set 和 expire两个命令的结果合集
     * @seconds 过期时间，单位s
     */
    public List<Object> setObject(String key, int seconds, Object value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        List<Object> result = null;
        Pipeline p = null;
        try {
            j = writePool.getResource();
            p = j.getShard(key).pipelined();
            p.set(SafeEncoder.encode(key), SerializaObjUtils.serialize(value));
            p.expire(key, seconds);
            result = p.syncAndReturnAll();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
            p = null;
        }
        return result;
    }

    /**
     * 执行getObject,获取通过setObject操作的key值,需要注意是会强制解压缩gzip
     *
     * @param key
     * @return Ojbect
     */
    public Object getObject(String key) throws Exception {
        return getObject(errorRetryTimes, key);
    }

    private Object getObject(int toTryCount, String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Object result = null;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            byte[] re = j.get(SafeEncoder.encode(key));
            if (re != null) {
                result = SerializaObjUtils.deserialize(re);
            }
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = getObject(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return result;
    }

    public String setex(String key, int seconds, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.setex(key, seconds, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long decrBy(String key, long integer) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.decrBy(key, integer);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long decr(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.decr(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 通过管道，执行decr和expire两个命令
     *
     * @param key
     * @param seconds 过期时间，单位s
     * @return List<Ojbect> 返回decr 和 expire两个命令的结果合集
     */
    public List<Object> decr(String key, int seconds) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        List<Object> result = null;
        Pipeline p = null;
        try {
            j = writePool.getResource();
            p = j.getShard(key).pipelined();
            p.decr(key);
            p.expire(key, seconds);
            result = p.syncAndReturnAll();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
            p = null;
        }
        return result;
    }


    public Long incrBy(String key, long integer) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.incrBy(key, integer);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public Long incr(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.incr(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 通过管道，执行incr和expire两个命令
     *
     * @param key
     * @param seconds 过期时间，单位s
     * @return List<Ojbect> 返回incr 和 expire两个命令的结果合集
     */
    public List<Object> incr(String key, int seconds) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        List<Object> result = null;
        Pipeline p = null;
        try {
            j = writePool.getResource();
            p = j.getShard(key).pipelined();
            p.incr(key);
            p.expire(key, seconds);
            result = p.syncAndReturnAll();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
            p = null;
        }
        return result;
    }

    /**
     * 返回shard中redis server info命令执行结果
     *
     * @return Map<String, SortedMap<String, String>>
     */
    public Map<String, SortedMap<String, String>> info() throws Exception {
        boolean flag = true;
        Map<String, SortedMap<String, String>> result = new HashMap<String, SortedMap<String, String>>();
        ShardedJedis master = null;
        ShardedJedis slave = null;
        SortedMap<String, String> re = null;
        try {
            // ShardedJedisPool[] pools = new ShardedJedisPool[]{writePool,readPool};
            master = writePool.getResource();
            for (Jedis jedis : master.getAllShards()) {
                re = new TreeMap<String, String>();
                for (String line : jedis.info().split("\\r\\n")) {
                    String[] kv = line.split(":");
                    re.put(kv[0], kv[1]);
                }
                //获取内存使用情况
                String memKey = null;
                for (String listStr : jedis.configGet("[max|s]*")) {
                    if (memKey != null) {
                        re.put(memKey, listStr);
                        memKey = null;
                    } else {
                        memKey = listStr;
                    }
                }
                result.put(jedis.getClient().getHost() + ":" + jedis.getClient().getPort(), re);
            }
            if (readPool != null) {
                slave = readPool.getResource();
                for (Jedis jedis : slave.getAllShards()) {
                    re = new TreeMap<String, String>();
                    for (String line : jedis.info().split("\\r\\n")) {
                        String[] kv = line.split(":");
                        re.put(kv[0], kv[1]);
                    }
                    //获取内存使用情况
                    String memKey = null;
                    for (String listStr : jedis.configGet("[max|s]*")) {
                        if (memKey != null) {
                            re.put(memKey, listStr);
                            memKey = null;
                        } else {
                            memKey = listStr;
                        }
                    }
                    result.put(jedis.getClient().getHost() + ":" + jedis.getClient().getPort(), re);
                }
            }
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(master);
            if (slave != null) {
                readPool.returnBrokenResource(slave);
            }
            throw new Exception(ex);
        } finally {
            if (flag) {
                writePool.returnResource(master);
                if (slave != null) {
                    readPool.returnResource(slave);
                }
            }
        }
        return result;
    }


    public Long append(String key, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.append(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String substr(String key, int start, int end) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.substr(key, start, end);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long hset(String key, String field, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hset(key, field, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 从redis hash key中获取一个对象
     *
     * @param key   redis hash类型中的key
     * @param field redis hash中的field
     * @return String
     */
    public String hget(String key, String field) throws Exception {
        return hget(errorRetryTimes, key, field);
    }

    private String hget(int toTryCount, String key, String field) throws Exception {
        String result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hget(key, field);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hget(toTryCount - 1, key, field);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long hsetnx(String key, String field, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hsetnx(key, field, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 往redis 中增加一个以key为名称 的hash set
     *
     * @param key  redis hash key
     * @param hash redis hash value
     * @return Return OK or Exception if hash is empty
     */
    public String hmset(String key, Map<String, String> hash) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.hmset(key, hash);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public List<String> hmget(String key, String... fields) throws Exception {
        return hmget(errorRetryTimes, key, fields);
    }

    private List<String> hmget(int toTryCount, String key, String... fields) throws Exception {
        List<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hmget(key, fields);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hmget(toTryCount - 1, key, fields);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 将redis hash key中一个对象加上value
     *
     * @param key   redis hash类型中的key
     * @param field redis hash中的field
     * @param value 需要key对应对象加上的值
     * @return 数字型，增加value后的结果
     */
    public Long hincrBy(String key, String field, long value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hincrBy(key, field, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Boolean hexists(String key, String field) throws Exception {
        Boolean result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            j = writePool.getResource();
            result = j.hexists(key, field);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public Long hdel(String key, String... field) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.hdel(key, field);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long hlen(String key) throws Exception {
        return hlen(errorRetryTimes, key);
    }

    private Long hlen(int toTryCount, String key) throws Exception {
        Long result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hlen(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hlen(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0)
                    readPool.returnResource(j);
                else
                    writePool.returnResource(j);
            }
        }
        return result;
    }

    public Set<String> hkeys(String key) throws Exception {
        return hkeys(errorRetryTimes, key);
    }

    private Set<String> hkeys(int toTryCount, String key) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hkeys(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hkeys(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public List<String> hvals(String key) throws Exception {
        return hvals(errorRetryTimes, key);
    }

    private List<String> hvals(int toTryCount, String key) throws Exception {
        List<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hvals(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hvals(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    /**
     * 获取redis hash 中key的全部对象
     *
     * @param key redis 中的key
     * @return Map
     */
    public Map<String, String> hgetAll(String key) throws Exception {
        return hgetAll(errorRetryTimes, key);
    }

    private Map<String, String> hgetAll(int toTryCount, String key) throws Exception {
        Map<String, String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.hgetAll(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = hgetAll(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    /**
     * 在redis list尾部增加一个String
     *
     * @param key
     * @param string
     * @return 数字型 ，返回增加到list的序号
     */
    public Long rpush(String key, String... string) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.rpush(key, string);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 根据 redis transaction处理 先del再rpush 操作,返回两个命令的执行结果
     *
     * @param key
     * @param string 数组
     * @return List<Object>
     * @throws Exception
     */
    public List<Object> rpushAfterDel(String key, String... string) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Transaction t;
        List<Object> result = null;
        try {
            j = writePool.getResource();
            t = j.getShard(key).multi();
            t.del(key);
            //因为jedis transaction没有批量操作，所以只能一个个处理
            for (String v : string) {
                t.rpush(key, v);
            }
            result = t.exec();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
            t = null;
        }
        return result;
    }

    public Long rpushx(String key, String string) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.rpushx(key, string);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 在redis list头部增加一个String
     *
     * @param key
     * @param string
     * @return 数字型 ，返回增加到list的序号
     */
    public Long lpush(String key, String... string) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.lpush(key, string);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    /**
     * 根据 redis transaction处理 先del再lpush 操作,返回两个命令的执行结果
     *
     * @param key
     * @param string 数组
     * @return List<Object>
     * @throws Exception
     */
    public List<Object> lpushAfterDel(String key, String... string) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Transaction t;
        List<Object> result = null;
        try {
            j = writePool.getResource();
            t = j.getShard(key).multi();
            t.del(key);
            //因为jedis transaction没有批量操作，所以只能一个个处理
            for (String v : string) {
                t.lpush(key, v);
            }
            result = t.exec();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
            t = null;
        }
        return result;
    }

    public Long lpushx(String key, String string) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.lpushx(key, string);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long llen(String key) throws Exception {
        return llen(errorRetryTimes, key);
    }

    private Long llen(int toTryCount, String key) throws Exception {
        Long result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.llen(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = llen(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public List<String> lrange(String key, long start, long end) throws Exception {
        return lrange(errorRetryTimes, key, start, end);
    }

    private List<String> lrange(int toTryCount, String key, long start, long end) throws Exception {
        List<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;

        try {

            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.lrange(key, start, end);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = lrange(toTryCount - 1, key, start, end);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public String ltrim(String key, long start, long end) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.ltrim(key, start, end);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public String lindex(String key, long index) throws Exception {
        return lindex(errorRetryTimes, key, index);
    }

    private String lindex(int toTryCount, String key, long index) throws Exception {
        String result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.lindex(key, index);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = lindex(toTryCount - 1, key, index);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public String lset(String key, long index, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.lset(key, index, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long lrem(String key, long count, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.lrem(key, count, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 从redis list头部取出一个key
     *
     * @param key
     * @return String
     */
    public String lpop(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.lpop(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * 从redis list尾部取出一个key
     *
     * @param key
     * @return String
     */
    public String rpop(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.rpop(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long linsert(String key, Client.LIST_POSITION where, String pivot, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.linsert(key, where, pivot, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;

    }

    public Long sadd(String key, String... member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.sadd(key, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Set<String> smembers(String key) throws Exception {
        return smembers(errorRetryTimes, key);
    }

    private Set<String> smembers(int toTryCount, String key) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.smembers(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = smembers(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }


    public Long srem(String key, String... member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.srem(key, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public String spop(String key) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.spop(key);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long scard(String key) throws Exception {
        return scard(errorRetryTimes, key);
    }

    public Long scard(int toTryCount, String key) throws Exception {
        Long result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.scard(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = scard(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Boolean sismember(String key, String member) throws Exception {
        return sismember(errorRetryTimes, key, member);
    }

    private Boolean sismember(int toTryCount, String key, String member) throws Exception {
        Boolean result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.sismember(key, member);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = sismember(toTryCount - 1, key, member);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public String srandmember(String key) throws Exception {
        return srandmember(errorRetryTimes, key);
    }

    private String srandmember(int toTryCount, String key) throws Exception {
        String result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.srandmember(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = srandmember(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }


    public Long zadd(String key, double score, String member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.zadd(key, score, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Set<String> zrange(String key, int start, int end) throws Exception {
        return zrange(errorRetryTimes, key, start, end);
    }

    private Set<String> zrange(int toTryCount, String key, int start, int end) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrange(key, start, end);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrange(toTryCount - 1, key, start, end);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Long zrem(String key, String... member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.zrem(key, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public Double zincrby(String key, double score, String member) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        double result = -1;
        try {
            j = writePool.getResource();
            result = j.zincrby(key, score, member);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long zrank(String key, String member) throws Exception {
        return zrank(errorRetryTimes, key, member);
    }

    private Long zrank(int toTryCount, String key, String member) throws Exception {
        Long result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrank(key, member);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrank(toTryCount - 1, key, member);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Long zrevrank(String key, String member) throws Exception {
        return zrevrank(errorRetryTimes, key, member);
    }

    private Long zrevrank(int toTryCount, String key, String member) throws Exception {
        Long result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrevrank(key, member);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrevrank(toTryCount - 1, key, member);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<String> zrevrange(String key, int start, int end) throws Exception {
        return zrevrange(errorRetryTimes, key, start, end);
    }

    private Set<String> zrevrange(int toTryCount, String key, int start, int end) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrevrange(key, start, end);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrevrange(toTryCount - 1, key, start, end);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end) throws Exception {
        return zrangeWithScores(errorRetryTimes, key, start, end);
    }

    private Set<Tuple> zrangeWithScores(int toTryCount, String key, int start, int end) throws Exception {
        Set<Tuple> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrangeWithScores(key, start, end);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrangeWithScores(toTryCount - 1, key, start, end);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) throws Exception {
        return zrevrangeWithScores(errorRetryTimes, key, start, end);
    }

    private Set<Tuple> zrevrangeWithScores(int toTryCount, String key, int start, int end) throws Exception {
        Set<Tuple> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrevrangeWithScores(key, start, end);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrevrangeWithScores(toTryCount - 1, key, start, end);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Long zcard(String key) throws Exception {
        return zcard(errorRetryTimes, key);
    }

    private Long zcard(int toTryCount, String key) throws Exception {
        Long result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zcard(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zcard(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Double zscore(String key, String member) throws Exception {
        return zscore(errorRetryTimes, key, member);
    }

    private Double zscore(int toTryCount, String key, String member) throws Exception {
        Double result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zscore(key, member);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zscore(toTryCount - 1, key, member);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public List<String> sort(String key) throws Exception {
        return sort(errorRetryTimes, key);
    }

    private List<String> sort(int toTryCount, String key) throws Exception {
        List<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.sort(key);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = sort(toTryCount - 1, key);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public List<String> sort(String key, SortingParams sortingParameters) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        List<String> result = null;
        try {
            j = writePool.getResource();
            result = j.sort(key, sortingParameters);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    public Long zcount(String key, double min, double max) throws Exception {
        return zcount(errorRetryTimes, key, min, max);
    }

    private Long zcount(int toTryCount, String key, double min, double max) throws Exception {
        Long result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zcount(key, min, max);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zcount(toTryCount - 1, key, min, max);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<String> zrangeByScore(String key, double min, double max) throws Exception {
        return zrangeByScore(errorRetryTimes, key, min, max);
    }

    private Set<String> zrangeByScore(int toTryCount, String key, double min, double max) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrangeByScore(key, min, max);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrangeByScore(toTryCount - 1, key, min, max);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) throws Exception {
        return zrevrangeByScore(errorRetryTimes, key, max, min);
    }

    private Set<String> zrevrangeByScore(int toTryCount, String key, double max, double min) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrevrangeByScore(key, max, min);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrevrangeByScore(toTryCount - 1, key, max, min);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) throws Exception {
        return zrangeByScore(errorRetryTimes, key, min, max, offset, count);
    }

    private Set<String> zrangeByScore(int toTryCount, String key, double min, double max, int offset, int count) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrangeByScore(key, min, max, offset, count);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrangeByScore(toTryCount - 1, key, min, max, offset, count);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) throws Exception {
        return zrevrangeByScore(errorRetryTimes, key, max, min, offset, count);
    }

    private Set<String> zrevrangeByScore(int toTryCount, String key, double max, double min, int offset, int count) throws Exception {
        Set<String> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrevrangeByScore(toTryCount - 1, key, max, min, offset, count);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) throws Exception {
        return zrangeByScoreWithScores(errorRetryTimes, key, min, max);
    }

    private Set<Tuple> zrangeByScoreWithScores(int toTryCount, String key, double min, double max) throws Exception {
        Set<Tuple> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrangeByScoreWithScores(key, min, max);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrangeByScoreWithScores(toTryCount - 1, key, min, max);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) throws Exception {
        return zrevrangeByScoreWithScores(errorRetryTimes, key, max, min);
    }

    private Set<Tuple> zrevrangeByScoreWithScores(int toTryCount, String key, double max, double min) throws Exception {
        Set<Tuple> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrevrangeByScoreWithScores(toTryCount - 1, key, max, min);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) throws Exception {
        return zrangeByScoreWithScores(errorRetryTimes, key, min, max, offset, count);
    }

    private Set<Tuple> zrangeByScoreWithScores(int toTryCount, String key, double min, double max, int offset, int count) throws Exception {
        Set<Tuple> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrangeByScoreWithScores(toTryCount - 1, key, min, max, offset, count);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) throws Exception {
        return zrevrangeByScoreWithScores(errorRetryTimes, key, max, min, offset, count);
    }

    private Set<Tuple> zrevrangeByScoreWithScores(int toTryCount, String key, double max, double min, int offset, int count) throws Exception {
        Set<Tuple> result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            result = j.zrevrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zrevrangeByScoreWithScores(toTryCount - 1, key, min, max, offset, count);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(key).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }


    public Long zremrangeByRank(String key, int start, int end) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.zremrangeByRank(key, start, end);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public Long zremrangeByScore(String key, double start, double end) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        Long result = null;
        try {
            j = writePool.getResource();
            result = j.zremrangeByScore(key, start, end);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public String set(byte[] key, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.set(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }


    public String set(String key, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        String result = null;
        try {
            j = writePool.getResource();
            result = j.set(key, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
        return result;
    }

    /**
     * set String,推荐使用setex
     *
     * @param key
     * @param seconds 过期时间
     * @return set 和 expire 两个命令的返回集合
     * @pamra value value
     */
    @Deprecated
    public List<Object> set(String key, int seconds, String value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        List<Object> result = null;
        Pipeline p = null;
        try {
            j = writePool.getResource();
            p = j.getShard(key).pipelined();
            p.set(key, value);
            p.expire(key, seconds);
            result = p.syncAndReturnAll();
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
            p = null;
        }
        return result;
    }

    public void zadd(byte[] key, int score, byte[] value) throws Exception {
        boolean flag = true;
        ShardedJedis j = null;
        try {
            j = writePool.getResource();
            j.zadd(key, score, value);
        } catch (Exception ex) {
            flag = false;
            writePool.returnBrokenResource(j);
            throw new Exception(ex + "," + j.getShardInfo(key).toString());
        } finally {
            if (flag) {
                writePool.returnResource(j);
            }
        }
    }

    public String zelementAtScore(String key, int score) throws Exception {
        byte[] temp = zelementAtScore(errorRetryTimes, SafeEncoder.encode(key), score);
        if (temp == null)
            return null;
        else
            return SafeEncoder.encode(temp);
    }

    public byte[] zelementAtScore(byte[] keyBA, int errorRetryTimes, int score) throws Exception {
        return zelementAtScore(errorRetryTimes, keyBA, score);
    }

    private byte[] zelementAtScore(int toTryCount, byte[] keyBA, int score) throws Exception {
        byte[] result = null;
        ShardedJedis j = null;
        boolean flag = true;
        try {
            if (toTryCount > 0) {
                j = readPool.getResource();
            } else {
                j = writePool.getResource();
            }
            Set<byte[]> temp = j.zrange(keyBA, score, score);
            if (!(temp.isEmpty()) && !(temp.size() > 1))
                result = temp.iterator().next();
        } catch (Exception ex) {
            flag = false;
            if (toTryCount > 0) {
                readPool.returnBrokenResource(j);
                result = zelementAtScore(toTryCount - 1, keyBA, score);
            } else {
                writePool.returnBrokenResource(j);
                throw new Exception(ex + "," + j.getShardInfo(keyBA).toString());
            }
        } finally {
            if (flag) {
                if (toTryCount > 0) {
                    readPool.returnResource(j);
                } else {
                    writePool.returnResource(j);
                }
            }
        }
        return result;
    }

    public void destroy() {
        if (writePool != null) {
            writePool.destroy();
        }
        if (readPool != null) {
            readPool.destroy();
        }
    }

    /**
     * 处理zookeeper data change event
     *
     * @param path
     * @param data
     * @since 1.0.3
     */
    public void handleDataChange(String path, Object data) {
        log.info("zookeeper handleDataChange event coming~");
        init(data);
        log.info("zookeeper handleDataChange event finish~");
    }

    /**
     * 处理zookeeper data delete event
     *
     * @param path
     * @since 1.0.3
     */
    public void handleDataDeleted(String path) {
        log.error("zookeeper handleDataDeleted method Unsupported!");

    }
}
