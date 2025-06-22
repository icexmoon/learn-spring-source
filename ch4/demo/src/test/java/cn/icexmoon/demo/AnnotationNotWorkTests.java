package cn.icexmoon.demo;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @ClassName AnnotationNotWorkTests
 * @Description 展示 @Autowired 等注解失效的情况
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 上午11:49
 * @Version 1.0
 */
@Slf4j
public class AnnotationNotWorkTests {
    @Slf4j
    @ToString
    static class Config{
        @Getter
        @Autowired
        private ApplicationContext ctx;
        @PostConstruct
        public void init(){
            log.info("bean initializing");
        }
    }

    @Test
    public void test(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        ctx.registerBean(CommonAnnotationBeanPostProcessor.class);
        ctx.registerBean(Config.class);
        ctx.refresh();
        log.info(ctx.getBean(Config.class).toString());
        ctx.close();
    }

    @Slf4j
    @ToString
    static class Config2{
        @Getter
        @Autowired
        private ApplicationContext ctx;
        @PostConstruct
        public void init(){
            log.info("bean initializing");
        }

        @Bean
        public BeanFactoryPostProcessor beanFactoryPostProcessor(){
            return beanFactory -> {
                log.info("beanFactoryPostProcessor is called.");
            };
        }
    }

    @Test
    public void test2(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        ctx.registerBean(CommonAnnotationBeanPostProcessor.class);
        ctx.registerBean(Config2.class);
        ctx.refresh();
        log.info(ctx.getBean(Config2.class).toString());
        ctx.close();
    }

    @Slf4j
    @ToString
    static class Config3{
        @Getter
        @Autowired
        private ApplicationContext ctx;
        @PostConstruct
        public void init(){
            log.info("bean initializing");
        }
    }

    static class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            log.info("beanFactoryPostProcessor is called.");
        }
    }

    @Test
    public void test3(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        ctx.registerBean(CommonAnnotationBeanPostProcessor.class);
        ctx.registerBean(Config3.class);
        ctx.registerBean(MyBeanFactoryPostProcessor.class);
        ctx.refresh();
        log.info(ctx.getBean(Config3.class).toString());
        ctx.close();
    }

    @Slf4j
    @ToString
    static class Config4 implements ApplicationContextAware, InitializingBean {
        @Getter
        private ApplicationContext ctx;

        @Bean
        public BeanFactoryPostProcessor beanFactoryPostProcessor(){
            return beanFactory -> {
                log.info("beanFactoryPostProcessor is called.");
            };
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.ctx = applicationContext;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("bean initializing");
        }
    }

    @Test
    public void test4(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        ctx.registerBean(CommonAnnotationBeanPostProcessor.class);
        ctx.registerBean(Config4.class);
        ctx.refresh();
        log.info(ctx.getBean(Config4.class).toString());
        ctx.close();
    }
}
