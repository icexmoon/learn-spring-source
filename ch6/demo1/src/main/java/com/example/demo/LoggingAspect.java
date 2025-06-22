package com.example.demo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @ClassName LoggingAspect
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午5:12
 * @Version 1.0
 */
@Aspect
@Component
public class LoggingAspect {

    // 定义切点：拦截所有 DemoService 方法
    @Pointcut("execution(* com.example.demo.DemoService.*(..))")
    public void serviceMethods() {}

    // 前置通知
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("方法执行前：" + joinPoint.getSignature().getName());
    }

    // 环绕通知（支持控制方法执行流程）
    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("方法执行前（环绕）");
        Object result = pjp.proceed(); // 执行目标方法
        System.out.println("方法执行后（环绕）");
        return result;
    }
}
