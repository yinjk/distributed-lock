package com.intellif.lock.redisson;

import com.intellif.lock.IfLock;
import com.intellif.lock.IfLockFactory;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;


/**
 * @author inori
 * @create 2018-11-26 14:51
 */
public class RedisLockFactory implements IfLockFactory {

    private RedisProperties redisProperties;

    private RedissonClient redissonClient;

    public RedisLockFactory(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
        init();
    }

    private void init() {
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel != null) {
            sentinelConfig();
        } else {
            singleConfig();
        }
    }

    @Override
    public IfLock getLock(String name) {
        RLock lock = redissonClient.getLock(name);
        return new RLockAdapter(lock);
    }


    private void singleConfig() {
        Config config = new Config();

        //获取timeout
        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);
        System.out.println("-------------" + timeoutValue);
        Integer timeout;
        if (null == timeoutValue) {
            timeout = null;
        } else if (!(timeoutValue instanceof Integer)) {
            Method millisMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
            timeout = ((Long) ReflectionUtils.invokeMethod(millisMethod, timeoutValue)).intValue();
        } else {
            timeout = (Integer) timeoutValue;
        }


        String prefix = "redis://";
        Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
        if (method != null && (Boolean)ReflectionUtils.invokeMethod(method, redisProperties)) {
            prefix = "rediss://";
        }

        config.useSingleServer()
                .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setConnectTimeout(timeout == null ? 1000 : timeout)
                .setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword());
        this.redissonClient = Redisson.create(config);
    }

    private void sentinelConfig() {
        Config config = new Config();
        SentinelServersConfig sentinelConfig = config.useSentinelServers();
        sentinelConfig
                .setMasterName(redisProperties.getSentinel().getMaster())
                .addSentinelAddress(redisProperties.getSentinel().getNodes().toArray(new String[0]))
                .setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword());
        this.redissonClient = Redisson.create(config);
    }

}