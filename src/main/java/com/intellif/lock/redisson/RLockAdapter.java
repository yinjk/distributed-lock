package com.intellif.lock.redisson;


import com.intellif.lock.IfLock;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @author inori
 * @create 2018-11-26 14:45
 */
public class RLockAdapter implements IfLock {

    private RLock rLock;

    public RLockAdapter(RLock rLock) {
        this.rLock = rLock;
    }

    @Override
    public boolean tryLock(long waitTime, long holdTime, TimeUnit unit) throws InterruptedException {
        return rLock.tryLock(waitTime, holdTime, unit);
    }

    @Override
    public void lock(long holdTime, TimeUnit unit) {
        rLock.lock(holdTime, unit);
    }

    @Override
    public boolean isLocked() {
        return rLock.isLocked();
    }

    @Override
    public int getHoldCount() {
        return rLock.getHoldCount();
    }

    @Override
    public void lock() {
        rLock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        rLock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return rLock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return rLock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        rLock.unlock();
    }

    @Override
    public Condition newCondition() {
        return rLock.newCondition();
    }
}