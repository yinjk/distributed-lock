package com.intellif.lock;

/**
 * the client to connect redis or zookeeper
 *
 * @author inori
 * @create 2018-11-22 20:04
 */
public interface IfLockFactory {

    /**
     *
     * @param name lock name
     * @return
     */
    IfLock getLock(String name);


}