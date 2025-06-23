package cn.icexmoon.demo3.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @ClassName LogAspect
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午8:27
 * @Version 1.0
 */
@Aspect
public class LogAspect {
    // 定义切点：拦截所有 Service 层方法
    @Pointcut("execution(* cn.icexmoon.demo3.controller.*.*(..))")
    public void controllerMethods() {}

    // 前置通知
    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("方法执行前：" + joinPoint.getSignature().getName());
    }

    // 环绕通知（支持控制方法执行流程）
    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("方法执行前（环绕）");
        Object result = pjp.proceed(); // 执行目标方法
        System.out.println("方法执行后（环绕）");
        return result;
    }
}
