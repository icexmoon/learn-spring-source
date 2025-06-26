package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @ClassName PointcutMatchTests
 * @Description 切点匹配机制
 * @Author icexmoon@qq.com
 * @Date 2025/6/25 上午11:12
 * @Version 1.0
 */
public class PointcutMatchTests {
    static class Target {
        public void sayHello() {
            System.out.println("hello");
        }

        public void sayGoodbye() {
            System.out.println("goodbye");
        }
    }

    static class Target2 {
        @Transactional
        public void sayHello() {
            System.out.println("hello");
        }

        public void sayGoodbye() {
            System.out.println("goodbye");
        }
    }

    @Transactional
    static class Target3 {
        public void sayHello() {
            System.out.println("hello");
        }

        public void sayGoodbye() {
            System.out.println("goodbye");
        }
    }

    @Transactional
    interface TargetInterface{
    }

    static class Target4 implements TargetInterface{
        public void sayHello() {
            System.out.println("hello");
        }

        public void sayGoodbye() {
            System.out.println("goodbye");
        }
    }

    @Test
    public void test() throws NoSuchMethodException {
        // 匹配指定方法
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.example.demo..sayHello(..))");
        Method targetHelloMethod = Target.class.getDeclaredMethod("sayHello");
        Method targetGoodbyeMethod = Target.class.getDeclaredMethod("sayGoodbye");
        Assertions.assertTrue(pointcut.matches(targetHelloMethod, Target.class));
        Assertions.assertFalse(pointcut.matches(targetGoodbyeMethod, Target.class));
    }

    @Test
    public void test2() throws NoSuchMethodException {
        // 匹配指定注解
        Class<?> TestCls = Target2.class;
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        Method targetHelloMethod = TestCls.getDeclaredMethod("sayHello");
        Method targetGoodbyeMethod = TestCls.getDeclaredMethod("sayGoodbye");
        Assertions.assertTrue(pointcut.matches(targetHelloMethod, TestCls));
        Assertions.assertFalse(pointcut.matches(targetGoodbyeMethod, TestCls));
    }

    static class MyMethodMatcherPointcut extends StaticMethodMatcherPointcut {
        private final Class<? extends Annotation> annotationClazz;

        public MyMethodMatcherPointcut(Class<? extends Annotation> annotationClazz) {
            this.annotationClazz = annotationClazz;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            // 查找方法上有没有注解
            MergedAnnotations annotations = MergedAnnotations.from(method);
            if (annotations.isPresent(annotationClazz)) {
                return true;
            }
            // 查找类、父类、实现的接口上有没有注解
            MergedAnnotations annotations2 = MergedAnnotations.from(targetClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
            if (annotations2.isPresent(annotationClazz)) {
                return true;
            }
            return false;
        }
    }

    @Test
    public void test3() throws NoSuchMethodException {
        // 匹配指定注解
        MyMethodMatcherPointcut pointcut = new MyMethodMatcherPointcut(Transactional.class);
        assertMethodMatcher(Target.class, pointcut, false, false);
        assertMethodMatcher(Target2.class, pointcut, true, false);
        assertMethodMatcher(Target3.class, pointcut, true, true);
        assertMethodMatcher(Target4.class, pointcut, true, true);
    }

    private static void assertMethodMatcher(Class<?> TestCls, MethodMatcher methodMatcher,
                                            boolean methodHelloExpire, boolean methodGoodByeExpire) throws NoSuchMethodException {
        Method targetHelloMethod = TestCls.getDeclaredMethod("sayHello");
        Method targetGoodbyeMethod = TestCls.getDeclaredMethod("sayGoodbye");
        Assertions.assertEquals(methodHelloExpire, methodMatcher.matches(targetHelloMethod, TestCls));
        Assertions.assertEquals(methodGoodByeExpire, methodMatcher.matches(targetGoodbyeMethod, TestCls));
    }
}
