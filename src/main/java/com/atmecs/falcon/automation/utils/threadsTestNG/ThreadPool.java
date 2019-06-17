package com.atmecs.falcon.automation.utils.threadsTestNG;

import java.util.HashMap;

public class ThreadPool {

    private static ThreadPool thisPool = new ThreadPool();
    private static HashMap<String, ThreadLocal<DriverInfo>> localMap =
            new HashMap<String, ThreadLocal<DriverInfo>>();

    public static void clear() {
        thisPool.userThreadLocal.remove();
    }

    public static DriverInfo get() {
        return thisPool.userThreadLocal.get();
    }

    public static DriverInfo getDriverInfo() {
        final String key = Thread.currentThread().getName();
        final ThreadLocal<DriverInfo> userThreadLocal = ThreadPool.localMap.get(key);
        if (userThreadLocal == null) {
            return null;
        }

        return userThreadLocal.get();
    }

    public static void set(DriverInfo dInfo) {

        thisPool.userThreadLocal.set(dInfo);
    }

    public static void setDriverInfo(DriverInfo dInfo) {
        final String key = Thread.currentThread().getName();
        System.out.println("currentThread name is:"+key);
        ThreadLocal<DriverInfo> userThreadLocal = ThreadPool.localMap.get(key);
        if (userThreadLocal == null) {
            userThreadLocal = new ThreadLocal<DriverInfo>();
        }
        userThreadLocal.set(dInfo);
        ThreadPool.localMap.put(key, userThreadLocal);
    }

    public final ThreadLocal<DriverInfo> userThreadLocal = new ThreadLocal<DriverInfo>();

}
