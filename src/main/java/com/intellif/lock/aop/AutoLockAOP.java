package com.intellif.lock.aop;

import com.intellif.lock.IfLock;
import com.intellif.lock.IfLockFactory;
import com.intellif.lock.annotation.IfLocked;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author inori
 * @create 2018-11-26 10:18
 */
@Aspect
@Component
public class AutoLockAOP {

    @Autowired
    private IfLockFactory ifLockFactory;

    @Pointcut("@annotation(com.intellif.lock.annotation.IfLocked)")
    private void lockAnnotation(){}

    @Around("lockAnnotation()")
    public Object aroundLock(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method targetMethod = methodSignature.getMethod();
        IfLocked annotation = targetMethod.getAnnotation(IfLocked.class);
        String lockName = annotation.value();
        int holdTime = annotation.holdTime();
        TimeUnit timeUnit = annotation.timeUnit();
        if (StringUtils.isEmpty(lockName)) {
            Class clazz = targetMethod.getClass();
            String className = clazz.getName();
            String methodName = targetMethod.getName();
            lockName = className + "-" + methodName;
        }
        IfLock lock = ifLockFactory.getLock(lockName);
        try {
            if (holdTime > 0) {
                lock.lock(holdTime, timeUnit);
            } else {
                lock.lock();
            }
            return pjp.proceed(pjp.getArgs());
        }finally {
            lock.unlock();
        }
    }
}