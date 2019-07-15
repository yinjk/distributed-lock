package com.intellif.lock;

import com.intellif.lock.zookeeper.ZKLockFactory;
import com.intellif.lock.zookeeper.config.ZkConfig;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author inori
 * @create 2018-11-24 17:09
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZookeeperLockTest {

    @Autowired
    private IfLockFactory ifLockFactory;

    /**
     * 测试zk锁的可重入性
     * @throws InterruptedException
     */
    @Test
    public void testLockAgain() throws InterruptedException {
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setAddr("192.168.11.84:32110");
        IfLockFactory client = ZKLockFactory.create(zkConfig);
        IfLock lock = client.getLock("zk_lock");
        lock.lock();
        System.out.println("test1 get lock ...");
        lock.lock();
        System.out.println("test1 get lock again ...");
        Thread.sleep(10000);
        lock.unlock();
        System.out.println("test1 释放了lock ...");
        lock.unlock();
        System.out.println("test1 释放了lock again ...");
        Thread.sleep(100000);
    }

    @Test
    public void testLock() throws InterruptedException {
        LocalTime start = LocalTime.now();
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setAddr("192.168.11.84:30021");
        IfLockFactory client = ZKLockFactory.create(zkConfig);
        System.out.println("create client:" + Duration.between(start, LocalTime.now()).toNanos());
        IfLock lock = client.getLock("zk_lock");
        System.out.println("get lock:" + Duration.between(start, LocalTime.now()).toNanos());
        lock.lock();
        System.out.println("lock:" + Duration.between(start, LocalTime.now()).toNanos());
        System.out.println("test1 get lock ...");
        System.out.println("print:" + Duration.between(start, LocalTime.now()).toNanos());
        lock.unlock();
        System.out.println("unlock:" + Duration.between(start, LocalTime.now()).toNanos());
        System.out.println("test1 释放了lock");
    }

    @Test
    public void testLocalLock() throws InterruptedException {
        LocalTime start = LocalTime.now();
        ReentrantLock lock = new ReentrantLock();
        System.out.println("create lock:" + Duration.between(start, LocalTime.now()).toNanos());
        lock.lock();
        System.out.println("lock:" + Duration.between(start, LocalTime.now()).toNanos());
        System.out.println("test1 get lock ...");
        System.out.println("print:" + Duration.between(start, LocalTime.now()).toNanos());
        lock.unlock();
        System.out.println("unlock:" + Duration.between(start, LocalTime.now()).toNanos());
        System.out.println("test1 释放了lock");
    }

    @Test
    public void testLockWithSleep() throws InterruptedException {
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setAddr("192.168.11.84:32110");
        IfLockFactory client = ZKLockFactory.create(zkConfig);
        IfLock lock = client.getLock("zk_lock");
        lock.lock();
        System.out.println("test1 get lock ...");
        Thread.sleep(100000);
        lock.unlock();
        System.out.println("test1 释放了lock");

    }

    @Test
    public void testCurrLock() throws InterruptedException {
        ZkConfig zkConfig = new ZkConfig();
        zkConfig.setAddr("192.168.11.84:32110");
        IfLockFactory client = ZKLockFactory.create(zkConfig);
        IfLock lock = client.getLock("zk_lock");
        Thread thread1 = new Thread(() -> testAsy(lock));
        Thread thread2 = new Thread(() -> testAsy(lock));
        Thread thread3 = new Thread(() -> testAsy(lock));
        Thread thread4 = new Thread(() -> testAsy(lock));
        Thread thread5 = new Thread(() -> testAsy(lock));
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        System.out.println("main Thread is " + Thread.currentThread().getId());
        Thread.sleep(100000);

    }

    public void testAsy(IfLock lock) {
        System.out.println("Thread: " + Thread.currentThread().getId() + " is wait lock");
        lock.lock();
        System.out.println("Thread: " + Thread.currentThread().getId() + " has get lock");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        lock.lock();
//        System.out.println("Thread: " + Thread.currentThread().getId() + " has get lock again");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("Thread: " + Thread.currentThread().getId() + " try to unlock");
        lock.unlock();
        System.out.println("Thread: " + Thread.currentThread().getId() + " released the lock");
    }

    @Test
    public void testConcurrent() throws InterruptedException {
//        ZkConfig zkConfig = new ZkConfig();
//        zkConfig.setAddr("192.168.11.84:32110");
//        IfLockFactory client = ZKLockFactory.create(zkConfig);
        Money money = new Money(ifLockFactory);
        ExecutorService es = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 10000; i++) {
            es.execute(() -> money.add());
        }
        Thread.sleep(5000);
        System.out.println(money.get());
    }


    @Test
    public void test5() throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper("192.168.11.84:32110", 3000, new DistributedLockApplicationTests.ZkWatcher());
        List<String> children = zk.getChildren("/locks/zk_lock", false);
        System.out.println(children);
    }


    @Test
    public void test() throws InterruptedException {
        Thread.sleep(10000);
        System.out.println("---");
    }
}