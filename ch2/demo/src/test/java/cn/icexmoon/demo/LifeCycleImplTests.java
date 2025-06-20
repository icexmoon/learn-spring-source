package cn.icexmoon.demo;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName LifeCycleImplTests
 * @Description 展示如何用模版方法模式实现生命周期钩子
 * @Author icexmoon@qq.com
 * @Date 2025/6/20 上午10:34
 * @Version 1.0
 */
public class LifeCycleImplTests {
    interface LifeCycleHook {
        void beforeConstruct();

        <T> void afterConstruct(T instance);
    }

    static class MyBeanFactory {
        private final List<LifeCycleHook> lifeCycleHooks = new ArrayList<>();

        public void add(LifeCycleHook lifeCycleHook) {
            lifeCycleHooks.add(lifeCycleHook);
        }

        public <T> T getBean(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            Constructor<T> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            // 调用实例化前钩子
            for (LifeCycleHook lifeCycleHook : lifeCycleHooks) {
                lifeCycleHook.beforeConstruct();
            }
            // 实例化
            T newInstance = constructor.newInstance();
            // 调用实例化后钩子
            for (LifeCycleHook lifeCycleHook : lifeCycleHooks) {
                lifeCycleHook.afterConstruct(newInstance);
            }
            return newInstance;
        }
    }

    @NoArgsConstructor
    static class User{
        private int id;
        private String name;
    }

    @Test
    public void testMyBeanFactory() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MyBeanFactory myBeanFactory = new MyBeanFactory();
        myBeanFactory.add(new LifeCycleHook() {
            @Override
            public void beforeConstruct() {
                System.out.println("beforeConstruct() is called.");
            }

            @Override
            public <T> void afterConstruct(T instance) {
                System.out.println("afterConstruct() is called.");
            }
        });
        myBeanFactory.getBean(User.class);
    }
}
