package cn.icexmoon.demo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @ClassName InitDestoryTests
 * @Description bean 创建和销毁的调用顺序
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午2:01
 * @Version 1.0
 */
public class InitDestroyTests {
    @Slf4j
    static class MyBean implements InitializingBean, DisposableBean {
        @PostConstruct
        public void init() {
            log.info("MyBean init by @PostConstruct");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("MyBean init by InitializingBean interface");
        }

        public void afterInstantiation(){
            log.info("MyBean init by @Bean");
        }

        @Override
        public void destroy() throws Exception {
            log.info("MyBean destroy by DisposableBean interface");
        }

        @PreDestroy
        public void preDestroy(){
            log.info("MyBean destroy by @PreDestroy");
        }

        public void beforeDestroy(){
            log.info("MyBean destroy by @Bean");
        }
    }

    @Configuration
    static class Config{
        @Bean(initMethod = "afterInstantiation", destroyMethod = "beforeDestroy")
        public MyBean myBean() {
            return new MyBean();
        }
    }

    @Test
    public void test() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(CommonAnnotationBeanPostProcessor.class);
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(Config.class);
        ctx.refresh();
        ctx.close();
    }
}
