package com.abc.editorserver.manager;

import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.support.LogEditor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * VersionManager
 * Created by Marco
 * Date: 2019/4/15 10:16
 */

public class VersionManager {

    private static VersionManager manager = new VersionManager();
    private Map<String, AtomicLong> tableDataVersions = new ConcurrentHashMap<>();

    private VersionManager() {}

    public static VersionManager getInstance() {
        return manager;
    }

    public static final String versionTableName = "VERSION";

    public void init() {
        LogEditor.serv.info("======开始加载版本号信息======");
        initTableDataVersion();
    }

    /**
     * 从redis中加载版本信息
     */
    private void initTableDataVersion() {
        JedisManager redisManager = JedisManager.getInstance();

        Map<String, String> redisData = redisManager.hgetAll(versionTableName);
        Iterator<String> iter = redisData.keySet().iterator();

        String dataKey, dataVersion;

        while (iter.hasNext()) {
            dataKey = iter.next();
            dataVersion = redisData.get(dataKey);

            tableDataVersions.put(dataKey, new AtomicLong(Long.valueOf(dataVersion)));
        }
    }

    /**
     * 获取指定的版本号信息
     * @param redisTableName
     * @param sn
     * @return
     */
    public String getTableDataVersion(String redisTableName, String sn) {
        String tableNameWithSn = redisTableName + ":" + sn;
        AtomicLong versionNumber = tableDataVersions.get(tableNameWithSn);

        // Lazy instantiation
        if (versionNumber == null) {
            JedisManager.getInstance().hset(versionTableName, tableNameWithSn, Long.toString(1L));
            tableDataVersions.put(tableNameWithSn, new AtomicLong(1L));
            return String.valueOf(1L);
        }

        return String.valueOf(versionNumber.get());
    }

    /**
     * 自增版本号
     * @param redisTableName
     * @param sn
     * @return
     */
    public String incrementTableDataVersion(String redisTableName, String sn) {
        String tableNameWithSn = redisTableName + ":" + sn;
        AtomicLong versionNumber = tableDataVersions.get(tableNameWithSn);

        if (versionNumber == null) {
            JedisManager.getInstance().hset(versionTableName, tableNameWithSn, Long.toString(1L));
            return String.valueOf(tableDataVersions.put(tableNameWithSn, new AtomicLong(1L)));
        }
        else {
            return String.valueOf(tableDataVersions.compute(tableNameWithSn, (k, v) -> {
                v.incrementAndGet();
                return v;
            }).get());
        }
    }

    /**
     * 检查当前数据版本号与提供的版本号是否一致
     * @param redisTableName
     * @param sn
     * @param verNum
     * @return
     */
    public boolean hasTableDataVersionChanged(String redisTableName, String sn, String verNum) {
        if (verNum.equals("ignore")) {
            return false;
        }
        String currVerNum = getTableDataVersion(redisTableName, sn);
        return !currVerNum.equals(verNum);
    }

    /**
     * 将缓存版本号信息写入Redis
     */
    public void versionCacheToRedis() {
        JedisManager redisManager = JedisManager.getInstance();
        Iterator<String> iter = tableDataVersions.keySet().iterator();
        String tableWithSn;

        while (iter.hasNext()) {
            tableWithSn = iter.next();
            redisManager.hset(versionTableName, tableWithSn, tableDataVersions.get(tableWithSn).toString());
        }
    }
}
