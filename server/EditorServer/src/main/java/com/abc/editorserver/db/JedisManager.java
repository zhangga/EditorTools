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
    static final ThreadLocal<Jedis> jedisConn = new ThreadLocal<>();

    private JedisManager() {}

    public static Jedis getCurrentPool() {
        Jedis conn;

        if ((conn = jedisConn.get()) != null) {
            return conn;
        }
        else {
            conn = new Jedis(EditorConfig.redis_ip, EditorConfig.redis_port, 10000, 3000);
            if (EditorConfig.redis_pwd.length() > 0) {
                conn.auth(EditorConfig.redis_pwd);
            }
            jedisConn.set(conn);
        }

        return conn;
    }

    public static void destoryPool() {
        Jedis conn = jedisConn.get();

        if (conn != null) {
            conn.close();
        }
    }

    /**
     * String存储
     * @param key
     * @param value
     * @return
     */
    public static String setKey(String key, String value) {
        return getCurrentPool().set(key, value);
    }

    /**
     * String读取
     * @param key
     * @return
     */
    public static String getKey(String key) {
        return getCurrentPool().get(key);
    }

    /**
     * 批量获取key  不支持通配符
     * @param keys
     * @return
     */
    public static List<String> mgetKeys(String... keys) {
        return getCurrentPool().mget(keys);
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
//			Jedis connection = jp.getCurrentPool();
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

    public static String hget(String key, String field) {
        return getCurrentPool().hget(key, field);
    }

    public static long hset(String key, String field, String value) {
        return getCurrentPool().hset(key, field, value);
    }

    public static long hsetNx(String key, String field, String value) {
        return getCurrentPool().hsetnx(key, field, value);
    }

    public static long hdel(String key, String field) {
        return getCurrentPool().hdel(key, field);
    }

    public static long hdel(String key, String... fields) {
        return getCurrentPool().hdel(key, fields);
    }

    public static long hincrBy(String key, String field, long value) {
        return getCurrentPool().hincrBy(key, field, value);
    }

    public static boolean hexists(String key, String field) {
        return getCurrentPool().hexists(key, field);
    }

    public static long push(String key, String field) {
        return getCurrentPool().lpush(key, field);
    }

    public static String pop(String key) {
        return getCurrentPool().rpop(key);
    }

    public static long incr(String key) {
        return getCurrentPool().incr(key);
    }

    public static String setEx(String key, String value, int expire) {
        return getCurrentPool().setex(key, expire, value);
    }

    public static void setEx(String key, int seconds) {
        getCurrentPool().expire(key, seconds);
    }

    public static Long del(String key) {
        return getCurrentPool().del(key);
    }

    public static Map<String, String> hgetAll(String key) {
        return getCurrentPool().hgetAll(key);
    }

    public static long zadd(String key, double score, String member) {
        return getCurrentPool().zadd(key, score, member);
    }

    public static long zadd(String key, Map<String, Double> scoreMembers) {
        return getCurrentPool().zadd(key, scoreMembers);
    }

    public static long zrem(String key, String member) {
        return getCurrentPool().zrem(key, member);
    }

    public static Long sadd(String key, String... member) {
        return getCurrentPool().sadd(key, member);
    }

    public static Set<String> smembers(String key) {
        return getCurrentPool().smembers(key);
    }

    public static long scard(String key) {
        Long size = getCurrentPool().scard(key);
        if (size == null)
            return 0;
        return size;
    }

    public static Long srem(String key,String member){
        return getCurrentPool().srem(key, member);
    }

    public static Long srem(String key,String... member){
        return getCurrentPool().srem(key, member);
    }

    //从小到大  索引从零开始  -1  不存在
    public static long zrank(String key, String member) {
        Long size = getCurrentPool().zrank(key, member);
        if (size == null)
            return -1;
        return size;
    }

    public static long zrevrank(String key, String member) {
        Long size = getCurrentPool().zrevrank(key, member);
        if (size == null)
            return -1;
        return size;
    }

    public static long zcount(String key, double min, double max) {
        Long size = getCurrentPool().zcount(key, min, max);
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
    public static Set<String> zrange(String key, long start, long end) {
        return getCurrentPool().zrange(key, start, end);
    }
    //按score从大到小
    public static Set<String> zrevrange(String key, long start, long end) {
        return getCurrentPool().zrevrange(key, start, end);
    }
    public static Set<Tuple> zrangeWithScores(String key, long start, long end) {
        return getCurrentPool().zrangeWithScores(key, start, end);
    }
    public static Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        return getCurrentPool().zrevrangeWithScores(key, start, end);
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public static boolean inRedis(String key) {
        return getCurrentPool().exists(key);
    }

    public static boolean inRedis(String key, String value) {
        return getCurrentPool().hexists(key, value);
    }

    /**
     * 发布
     * @param channel
     * @param message
     */
    public static void publish(String channel, String message) {
        getCurrentPool().publish(channel, message);
    }

    /**
     * 订阅
     * @param channel
     */
    public static void subscribe(String channel, JedisPubSub sub) {
//		jc.psubscribe(sub, channel);//带通配符
        getCurrentPool().subscribe(sub, channel);
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
//			Jedis j = p.getValue().getCurrentPool();
//			Set<String> keys = j.keys("*");
//			endSet.addAll(keys);
//		}
//		return endSet;
//	}

}
