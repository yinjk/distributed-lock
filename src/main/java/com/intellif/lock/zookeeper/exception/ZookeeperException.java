package com.intellif.lock.zookeeper.exception;

/**
 * @author inori
 * @create 2018-11-23 17:16
 */
public class ZookeeperException extends RuntimeException {

    public ZookeeperException() {
    }

    public ZookeeperException(String message) {
        super(message);
    }

    public ZookeeperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZookeeperException(Throwable cause) {
        super(cause);
    }

    public ZookeeperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}