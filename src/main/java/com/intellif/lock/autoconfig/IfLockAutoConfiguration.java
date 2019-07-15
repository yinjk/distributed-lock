package com.intellif.lock.autoconfig;

import com.intellif.lock.IfLockFactory;
import com.intellif.lock.redisson.RedisLockFactory;
import com.intellif.lock.zookeeper.ZKLockFactory;
import com.intellif.lock.zookeeper.config.ZkConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author inori
 * @create 2018-11-24 20:58
 */
@Configuration
@EnableConfigurationProperties({IfLockProperties.class, RedisProperties.class})
public class IfLockAutoConfiguration {

    private static final String ZK = "zookeeper";

    private static final String REDIS = "redis";

    @Autowired
    private IfLockProperties ifLockProperties;

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public IfLockFactory ifLockFactory() {
        String useServer = ifLockProperties.getUseServer();
        if (ZK.equals(useServer)) { //使用zookeeper作为分布锁服务端
            ZkConfig zkConfig = new ZkConfig();
            zkConfig.setAddr(ifLockProperties.getZkAddr());
            zkConfig.setRoot(ifLockProperties.getZkRoot());
            zkConfig.setSessionTimeout(ifLockProperties.getZkTimeout());
            return ZKLockFactory.create(zkConfig);
        }
        //默认使用redis作为分布式锁服务端
        return new RedisLockFactory(redisProperties);
    }

}