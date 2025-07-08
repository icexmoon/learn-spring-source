package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * @ClassName ImportTests
 * @Description Bean 定义覆盖
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 下午12:20
 * @Version 1.0
 */
public class ImportTests4 {
    static class OuterBean1 {
    }

    static class OuterBean2 {
    }

    @AllArgsConstructor
    @ToString
    static class SameBean{
        private String source;
    }

    /**
     * 外部配置类1
     */
    @Configuration
    public static class OuterConfig1 {
        @Bean
        public OuterBean1 outerBean1() {
            return new OuterBean1();
        }

        @Bean
        @ConditionalOnMissingBean
        public SameBean sameBean(){
            return new SameBean("第三方");
        }
    }

    /**
     * 外部配置类2
     */
    @Configuration
    public static class OuterConfig2 {
        @Bean
        public OuterBean2 outerBean2() {
            return new OuterBean2();
        }
    }

    /**
     * 本项目的配置类
     */
    @Configuration
    @Import(MyImportSelector.class)
    static class Config {
        @Bean
        public SameBean sameBean(){
            return new SameBean("本项目");
        }
    }

    static class MyImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            // 从配置文件读取需要导入的类
            List<String> classNames = SpringFactoriesLoader.loadFactoryNames(MyImportSelector.class, this.getClass().getClassLoader());
            // 返回需要导入的类的名称
            return classNames.toArray(new String[0]);
        }
    }

    @Test
    public void test() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.setAllowBeanDefinitionOverriding(false);
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(Config.class);
        ctx.refresh();
        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        SameBean sameBean = ctx.getBean(SameBean.class);
        System.out.println(sameBean);
        ctx.close();
    }
}
