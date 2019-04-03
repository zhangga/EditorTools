package com.abc.editorserver.db;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.support.LogEditor;
import redis.clients.jedis.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class JedisManager {

    private static JedisManager mgr = new JedisManager();

    public static JedisManager getInstance() {
        return mgr;
    }

    private JedisManager() {

    }

    private Jedis jedis = null;

    public void createPool() {
        jedis = new Jedis(EditorConfig.redis_ip, EditorConfig.redis_port, 10000, 3000);
        if (EditorConfig.redis_pwd.length() > 0) {
            jedis.auth(EditorConfig.redis_pwd);
        }
    }

    public void destoryPool() {
    }

    public Jedis getResource() {
        if (jedis == null) {
            createPool();
        }
        return jedis;
    }

    /**
     * String存储
     * @param key
     * @param value
     * @return
     */
    public String setKey(String key, String value) {
        return getResource().set(key, value);
    }

    /**
     * String读取
     * @param key
     * @return
     */
    public String getKey(String key) {
        return getResource().get(key);
    }

    /**
     * 批量获取key  不支持通配符
     * @param keys
     * @return
     */
    public List<String> mgetKeys(String... keys) {
        return getResource().mget(keys);
    }

//	/**
//	 * 须要估量 不能太大
//	 * @param pattern
//	 * @return
//	 */
//	public TreeSet<String> getKeys(String pattern) {
//		TreeSet<String> keys = new TreeSet<>();
//		Map<String, JedisPool> clusterNodes = jc.getClusterNodes();
//		for (String k : clusterNodes.keySet()) {
//			JedisPool jp = clusterNodes.get(k);
//			Jedis connection = jp.getResource();
//			try {
//				keys.addAll(connection.keys(pattern));
//			} catch (Exception e) {
//				logger.error("Getting keys error: {}", e);
//			} finally {
//				logger.debug("Connection closed.");
//				connection.close();
//			}
//		}
//		return keys;
//	}

    public String hget(String key, String field) {
        return getResource().hget(key, field);
    }

    public long hset(String key, String field, String value) {
        return getResource().hset(key, field, value);
    }

    public long hdel(String key, String field) {
        return getResource().hdel(key, field);
    }

    public long hdel(String key, String... fields) {
        return getResource().hdel(key, fields);
    }

    public long hincrBy(String key, String field, long value) {
        return getResource().hincrBy(key, field, value);
    }

    public long push(String key, String field) {
        return getResource().lpush(key, field);
    }

    public String pop(String key) {
        return getResource().rpop(key);
    }

    public long incr(String key) {
        return getResource().incr(key);
    }

    public String setEx(String key, String value, int expire) {
        return getResource().setex(key, expire, value);
    }

    public void setEx(String key, int seconds) {
        getResource().expire(key, seconds);
    }

    public Long del(String key) {
        return getResource().del(key);
    }

    public Map<String, String> hgetAll(String key) {
        return getResource().hgetAll(key);
    }

    public long zadd(String key, double score, String member) {
        return getResource().zadd(key, score, member);
    }

    public long zadd(String key, Map<String, Double> scoreMembers) {
        return getResource().zadd(key, scoreMembers);
    }

    public long zrem(String key, String member) {
        return getResource().zrem(key, member);
    }

    public Long sadd(String key, String... member) {
        return getResource().sadd(key, member);
    }

    public Set<String> smembers(String key) {
        return getResource().smembers(key);
    }

    public long scard(String key) {
        Long size = getResource().scard(key);
        if (size == null)
            return 0;
        return size;
    }

    public Long srem(String key,String member){
        return getResource().srem(key, member);
    }

    public Long srem(String key,String... member){
        return getResource().srem(key, member);
    }

    //从小到大  索引从零开始  -1  不存在
    public long zrank(String key, String member) {
        Long size = getResource().zrank(key, member);
        if (size == null)
            return -1;
        return size;
    }

    public long zrevrank(String key, String member) {
        Long size = getResource().zrevrank(key, member);
        if (size == null)
            return -1;
        return size;
    }

    public long zcount(String key, double min, double max) {
        Long size = getResource().zcount(key, min, max);
        if (size == null)
            return 0;
        return size;
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。按score从小到大
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrange(String key, long start, long end) {
        return getResource().zrange(key, start, end);
    }
    //按score从大到小
    public Set<String> zrevrange(String key, long start, long end) {
        return getResource().zrevrange(key, start, end);
    }
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        return getResource().zrangeWithScores(key, start, end);
    }
    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        return getResource().zrevrangeWithScores(key, start, end);
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean inRedis(String key) {
        return getResource().exists(key);
    }

    public boolean inRedis(String key, String value) {
        return getResource().hexists(key, value);
    }

    /**
     * 发布
     * @param channel
     * @param message
     */
    public void publish(String channel, String message) {
        getResource().publish(channel, message);
    }

    /**
     * 订阅
     * @param channel
     */
    public void subscribe(String channel, JedisPubSub sub) {
//		jc.psubscribe(sub, channel);//带通配符
        getResource().subscribe(sub, channel);
    }

    public void test() {
        setKey("test", "zhangga");
        LogEditor.db.info("redis test get key: test -> {}", getKey("test"));
    }

//	/**
//	 * 获取所有的键
//	 * FIXME ？？？不能实际用
//	 * @return
//	 */
//	public Set<String> getAllKeys() {
//		Set<String> endSet = new HashSet<>();
//		Map<String, JedisPool> map = jc.getClusterNodes();
//
//		//循环所有的集群节点，获取所有的key
//		for(Entry<String, JedisPool> p : map.entrySet())
//		{
//			Jedis j = p.getValue().getResource();
//			Set<String> keys = j.keys("*");
//			endSet.addAll(keys);
//		}
//		return endSet;
//	}

}
