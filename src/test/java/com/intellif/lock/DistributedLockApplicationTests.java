package com.intellif.lock;

import com.intellif.lock.zookeeper.ZKLockFactory;
import com.intellif.lock.zookeeper.config.ZkConfig;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DistributedLockApplicationTests {
	private String root = "/locks";//根

	@Test
	public void contextLoads() throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper("192.168.11.84:32110", 30000, new ZkWatcher());
		Stat stat = zk.exists(root, false);
		zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		if(stat == null){
			// 创建根节点
			zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}

	}

	@Test
	public void test1() throws InterruptedException {
		ZkConfig zkConfig = new ZkConfig();
		zkConfig.setAddr("192.168.11.84:32110");
		IfLockFactory client = ZKLockFactory.create(zkConfig);
		IfLock lock = client.getLock("lock");
		lock.lock();
		System.out.println("test1 get lock ...");
		lock.lock();
		System.out.println("test1 get lock again ...");
		Thread.sleep(100000);
		lock.unlock();
		System.out.println("test1 释放了lock");
	}

	@Test
	public void test2() {
		ZkConfig zkConfig = new ZkConfig();
		zkConfig.setAddr("192.168.11.84:32110");
		IfLockFactory client = ZKLockFactory.create(zkConfig);
		IfLock lock = client.getLock("lock");
		lock.lock();
		System.out.println("test2 get lock...");
		lock.unlock();
		System.out.println("test2 释放了lock");
	}

	@Test
	public void test3() throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper("192.168.11.84:32110", 3000, new ZkWatcher());
		zk.delete(root+"/test", -1);
		zk.create(root+"/test", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create(root+"/test/test1", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		zk.create(root+"/test/test2", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		zk.delete(root+"/test/test1", -1);
		zk.delete(root+"/test", -1);
		List<String> children = zk.getChildren(root + "/test", false);
		System.out.println(children);
	}

	@Test
	public void test5() throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper("192.168.11.84:32110", 3000, new ZkWatcher());
		List<String> children = zk.getChildren(root + "/zk_lock", false);
		System.out.println(children);
	}

	@Test
	public void test4() {
		List<String> list = new ArrayList(Arrays.asList("a", "b", "v"));
		int b = Collections.binarySearch(list, "b");
		System.out.println(b);
	}

	public static class ZkWatcher implements Watcher {
		@Override
		public void process(WatchedEvent watchedEvent) {
			System.out.println(watchedEvent.getPath());
			System.out.println(watchedEvent.getState());
			System.out.println(watchedEvent.getType());
			System.out.println(watchedEvent);
		}
	}
}
