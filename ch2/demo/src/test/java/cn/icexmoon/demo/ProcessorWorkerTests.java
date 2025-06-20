package cn.icexmoon.demo;

import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName ProsessorWorckerTests
 * @Description 探讨 bean 后处理器的工作原理
 * @Author icexmoon@qq.com
 * @Date 2025/6/20 下午4:21
 * @Version 1.0
 */
public class ProcessorWorkerTests {
    static class Bean1 {
    }

    static class Bean2 {
    }

    @ToString
    static class Bean3 {
        @Autowired
        private Bean1 bean1;
        private Bean2 bean2;
        private String javaHome;

        @Autowired
        public void setBean2(Bean2 bean2) {
            this.bean2 = bean2;
        }

        @Autowired
        public void setJavaHome(@Value("${JAVA_HOME}") String javaHome) {
            this.javaHome = javaHome;
        }
    }

    @Test
    public void test() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("bean1", new Bean1());
        beanFactory.registerSingleton("bean2", new Bean2());
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 添加对`${...}`表达式的解析器
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders);
        AutowiredAnnotationBeanPostProcessor beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        // 关联 bean 工厂
        Bean3 bean3 = new Bean3();
        beanPostProcessor.setBeanFactory(beanFactory);
        // 模拟 bean 的后处理器调用
        beanPostProcessor.postProcessProperties(null, bean3, "bean3");
        System.out.println(bean3);
    }

    @Test
    public void test2() throws Throwable {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("bean1", new Bean1());
        beanFactory.registerSingleton("bean2", new Bean2());
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 添加对`${...}`表达式的解析器
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders);
        AutowiredAnnotationBeanPostProcessor beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        // 关联 bean 工厂
        Bean3 bean3 = new Bean3();
        beanPostProcessor.setBeanFactory(beanFactory);
        // 模拟 bean 的后处理器调用
        Method findAutowiringMetadata = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata", String.class, Class.class, PropertyValues.class);
        findAutowiringMetadata.setAccessible(true);
        InjectionMetadata metadata = (InjectionMetadata) findAutowiringMetadata.invoke(beanPostProcessor, "bean3", Bean3.class, null);
        metadata.inject(bean3, "bean3", null);
        System.out.println(bean3);
    }

    @Test
    public void test3() throws Throwable {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("bean1", new Bean1());
        beanFactory.registerSingleton("bean2", new Bean2());
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 添加对`${...}`表达式的解析器
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders);
        AutowiredAnnotationBeanPostProcessor beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        // 关联 bean 工厂
        Bean3 bean3 = new Bean3();
        beanPostProcessor.setBeanFactory(beanFactory);
        // 模拟 bean 的后处理器调用
        Method findAutowiringMetadata = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata", String.class, Class.class, PropertyValues.class);
        findAutowiringMetadata.setAccessible(true);
        InjectionMetadata metadata = (InjectionMetadata) findAutowiringMetadata.invoke(beanPostProcessor, "bean3", Bean3.class, null);
        // 模拟 inject 方法调用
        mockFiledInject(beanFactory, bean3);
        mockMethodInject(beanFactory, bean3);
        mockMethodValueInject(beanFactory, bean3);
        System.out.println(bean3);
    }

    private static void mockMethodValueInject(DefaultListableBeanFactory beanFactory, Bean3 bean3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 模拟 方法注入 Value
        Method setJavaHome = Bean3.class.getDeclaredMethod("setJavaHome", String.class);
        String javaHome = (String) beanFactory.doResolveDependency(new DependencyDescriptor(new MethodParameter(setJavaHome, 0), true), null, null, null);
        setJavaHome.invoke(bean3, javaHome);
    }

    private static void mockMethodInject(DefaultListableBeanFactory beanFactory, Bean3 bean3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 模拟方法注入
        Method setBean2 = Bean3.class.getDeclaredMethod("setBean2", Bean2.class);
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(new MethodParameter(setBean2, 0), true);
        Bean2 bean2 = (Bean2) beanFactory.doResolveDependency(dependencyDescriptor, null, null, null);
        setBean2.invoke(bean3, bean2);
    }

    private static void mockFiledInject(DefaultListableBeanFactory beanFactory, Bean3 bean3) throws NoSuchFieldException, IllegalAccessException {
        // 模拟属性注入
        Field bean1Field = Bean3.class.getDeclaredField("bean1");
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(bean1Field, true);
        Bean1 bean1 = (Bean1) beanFactory.doResolveDependency(dependencyDescriptor, null, null, null);
        bean1Field.set(bean3, bean1);
    }
}
