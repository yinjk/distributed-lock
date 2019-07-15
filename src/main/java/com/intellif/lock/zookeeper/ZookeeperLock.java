package com.intellif.lock.zookeeper;

import com.intellif.lock.AbstractIfLock;
import com.intellif.lock.LockHolder;
import com.intellif.lock.zookeeper.exception.ZookeeperException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * @author inori
 * @create 2018-11-22 20:13
 */
public class ZookeeperLock extends AbstractIfLock {

    private static final String SEPARATOR = "/";
    private static final String LOCK_OWNER_PRE = "_locks_";

    private ZooKeeper zk;
    private String root;
    private ThreadLocal<String> currLock = new ThreadLocal<>();
    private ThreadLocal<String> preLock = new ThreadLocal<>();
    private AtomicInteger lockHold = new AtomicInteger(1);


    public ZookeeperLock(ZooKeeper zk,String root, String name) {
        super(name);
        this.root = root;
        this.zk = zk;
    }

    @Override
    public void lock() {
        if (!tryLock()) { //没有获取到锁，一直等待
            waitForLock();
        }
    }

    private void waitForLock() {
        String preLocks = this.preLock.get();
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Stat stat = zk.exists(preLocks, event -> latch.countDown());
            if (stat == null) { //前一个节点已经被删除了
                return;
            }
            latch.await();
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    private boolean waitForLock(long time, TimeUnit unit) {
        String preLocks = this.preLock.get();
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Stat stat = zk.exists(preLocks, event -> latch.countDown());
            if (stat == null) { //前一个节点已经被删除了
                return true;
            }
            boolean await = latch.await(time, unit);
            if (!await) { //没有获取到锁
                tryDeleteLockNode(currLock.get(), false);
                this.currLock.remove();
                this.preLock.remove();
            }
            return await;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        String lockFullName = root + SEPARATOR + name;
        createLockNode(lockFullName);
        String lockOwner = lockFullName + SEPARATOR + LOCK_OWNER_PRE;
        if (null != currLock.get()) { //如果该线程已经持有该锁 则直接返回true，（实现可重入锁）
            lockHold.incrementAndGet();
            return true;
        }
        try {
            this.currLock.set(zk.create(lockOwner, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));
            List<String> children = zk.getChildren(lockFullName, false);
            Collections.sort(children);
            String holdLock = children.get(0); // 取出锁下面最小的node，该node是当前锁的持有者
            if (Objects.equals(root + SEPARATOR + name + SEPARATOR + holdLock, currLock.get())) { //如果当前线程的锁等于zk上的锁持有者,表示获取锁成功
                return true;
            } else {
                //取出当前线程的前一个node
                String currLocks = currLock.get();
                String currNode = currLocks.substring(currLocks.lastIndexOf(SEPARATOR) + 1);
                String preLocks = children.get(Collections.binarySearch(children, currNode) - 1);
                this.preLock.set(root + SEPARATOR + name + SEPARATOR + preLocks);
            }
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        boolean locked;
        locked = tryLock();
        if (locked) {
            return true;
        }
        return waitForLock(time, unit);
    }

    private void createLockNode(String lockFullName) {
        try {
            Stat stat = zk.exists(lockFullName, false);
            if (stat == null) {
                zk.create(lockFullName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            throw new ZookeeperException(e);
        } catch (KeeperException e) {
            if (!Objects.equals(e.code(), KeeperException.Code.NODEEXISTS)) {
                throw new ZookeeperException(e);
            }
        }
    }

    private void tryDeleteLockNode(String lockFullName, boolean checkChild) {
        try {
            if (checkChild) {
                List<String> children = zk.getChildren(lockFullName, false);
                if (CollectionUtils.isEmpty(children)) { //该锁下已经没有持有者和等待者，删除该锁
                    zk.delete(lockFullName, -1);
                    LockHolder.remove(this.name);
                }
            } else { //不用检查该node下面是否还有child，直接删除该node
                zk.delete(lockFullName, -1);
            }
        } catch (KeeperException e) {
            /*
             * doing nothing... 删除时发生异常，可能是getChildren的时候发现该锁节点下没有持有者节点了，
             * 但是在执行删除操作前另一个线程/进程获取了该锁，并在该锁节点下创建了持有者节点，在这种情况下删除锁节点
             * 由于该节点下还有子节点会抛出NotEmptyException异常，此时什么都不做，将删除锁节点的操作交给新的持有
             */

        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void unlock() {
        if (lockHold.get() > 1) {
            lockHold.decrementAndGet();
            return;
        }
        String currentLock = currLock.get();
        if (StringUtils.isEmpty(currentLock)) {
            return;
        }
        try {
            currLock.remove();
            zk.delete(currentLock, -1);
            tryDeleteLockNode(root + SEPARATOR + name, true);
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public boolean tryLock(long waitTime, long timeOut, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void lock(long timeOut, TimeUnit unit) {
        if (!tryLock()) { //没有获取到锁，一直等待
            waitForLock();
        }
    }

    @Override
    public boolean isLocked() {
        String lockFullName = root + SEPARATOR + name;
        try {
            List<String> children = zk.getChildren(lockFullName, false);
            return !CollectionUtils.isEmpty(children);
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public int getHoldCount() {
        return lockHold.get();
    }

}