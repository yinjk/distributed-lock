package com.intellif.lock.zookeeper.config;

/**
 * @author inori
 * @create 2018-11-22 20:21
 */

public class ZkConfig {

    private String root = "/locks";

    private int sessionTimeout = 3000;

    private String addr = "localhost:2181";

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}