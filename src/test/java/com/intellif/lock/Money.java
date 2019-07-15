package com.intellif.lock;

import com.intellif.lock.annotation.IfLocked;
import org.springframework.stereotype.Component;

/**
 * @author inori
 * @create 2018-11-24 20:29
 */

@Component
public class Money {
    private IfLockFactory ifLockFactory;

    public Money(IfLockFactory ifLockFactory) {
        this.ifLockFactory = ifLockFactory;
    }

    int a = 0;

    @IfLocked()
    public void add() {
        IfLock zk_lock = ifLockFactory.getLock("zk_lock");
        zk_lock.lock();
        a++;
        zk_lock.unlock();
    }

    public int get() {
        return a;
    }
}