package com.intellif.lock.zookeeper;

import com.intellif.lock.IfLock;
import com.intellif.lock.IfLockFactory;
import com.intellif.lock.LockHolder;
import com.intellif.lock.zookeeper.config.ZkConfig;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * the distributed lock base on zookeeper
 *
 * @author inori
 * @create 2018-11-22 18:22
 */
public class ZKLockFactory implements IfLockFactory, Watcher {

    private ZooKeeper zk;

    private String root;

    public static ZKLockFactory create() {
        return new ZKLockFactory(new ZkConfig());
    }

    public static ZKLockFactory create(ZkConfig zkConfig) {
        return new ZKLockFactory(zkConfig);
    }

    @Override
    public IfLock getLock(String name) {
        if (LockHolder.contains(name)) {
            return LockHolder.get(name);
        }
        ZookeeperLock zookeeperLock = new ZookeeperLock(zk, root, name);
        LockHolder.add(name, zookeeperLock);
        return zookeeperLock;
    }


    public ZKLockFactory(ZkConfig zkConfig) {
        this.root = zkConfig.getRoot();
        try {
            zk = new ZooKeeper(zkConfig.getAddr(), zkConfig.getSessionTimeout(), this);
            Stat stat = zk.exists(root, false);
            if(stat == null){
                // 创建根节点
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    @Override
    public void process(WatchedEvent event) {

    }
}