package com.intellif.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author inori
 * @create 2018-11-22 20:07
 */
public interface IfLock extends Lock {

    /**
     * 尝试获取锁，最多等待waitTime时间，在达到最大等待时间之后无论是否获得锁都将立即返回
     * @param waitTime 获取锁等待的最长时间
     * @param holdTime 锁持有的最长时间
     * @param unit 时间单位
     * @return 是否获取锁成功
     * @throws InterruptedException 等待时可被中断
     */
    @Deprecated
    boolean tryLock(long waitTime, long holdTime, TimeUnit unit) throws InterruptedException;


    /**
     * 获取锁，若无法获取将一直等待，锁的最长持有时间为timeOut，达到最长持有时间会自动释放锁
     * @param holdTime 锁持有最长时间
     * @param unit 时间单位
     */
    @Deprecated
    void lock(long holdTime, TimeUnit unit);

    /**
     * 判断该线程是否被锁定
     * @return 是否锁定
     */
    boolean isLocked();

    /**
     * 获取当前线程重入次数
     * @return 重入次数
     */
    int getHoldCount();
}
