package com.intellif.lock.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author inori
 * @create 2018-11-24 20:59
 */
@ConfigurationProperties("intellif.lock")
public class IfLockProperties {

    private String useServer = "redis";

    private String zkAddr = "localhost:2181";

    private int zkTimeout = 3000;

    private String zkRoot = "/locks";

    public String getUseServer() {
        return useServer;
    }

    public void setUseServer(String useServer) {
        this.useServer = useServer;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    public int getZkTimeout() {
        return zkTimeout;
    }

    public void setZkTimeout(int zkTimeout) {
        this.zkTimeout = zkTimeout;
    }

    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }
}