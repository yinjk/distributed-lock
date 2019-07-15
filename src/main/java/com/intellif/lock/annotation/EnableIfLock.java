package com.intellif.lock.annotation;

import com.intellif.lock.autoconfig.IfLockAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author inori
 * @create 2018-11-26 19:01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(IfLockAutoConfiguration.class)
public @interface EnableIfLock {
}