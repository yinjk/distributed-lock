package com.intellif.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author inori
 * @create 2018-11-24 17:00
 */
public abstract class LockHolder {

    private LockHolder(){}

    private static Map<String, IfLock> locks = new ConcurrentHashMap<>();

    public static void add(String key, IfLock lock) {
        locks.put(key, lock);
    }

    public static IfLock get(String key) {
        return locks.get(key);
    }

    public static boolean contains(String key) {
        boolean b = locks.containsKey(key);
        if (b) {
            IfLock ifLock = locks.get(key);
            if (ifLock != null) {
                return true;
            } else {
                locks.remove(key);
            }
        }
        return false;
    }

    public static void remove(String key) {
        locks.remove(key);
    }
}