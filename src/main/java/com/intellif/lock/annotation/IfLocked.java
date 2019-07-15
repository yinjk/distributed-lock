package com.intellif.lock.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface IfLocked {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    int holdTime() default 0;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
