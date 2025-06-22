package cn.icexmoon.demo;

import jakarta.annotation.PostConstruct;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @ClassName AwareTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 上午9:28
 * @Version 1.0
 */
@Slf4j
public class AwareTests {
    @ToString
    @Slf4j
    static class MyBean implements ApplicationContextAware, BeanNameAware, InitializingBean {
        private String beanName;
        private ApplicationContext applicationContext;

        @Override
        public void setBeanName(String name) {
            this.beanName = name;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            // bean 被依赖注入后调用
            log.info("Initializing Bean");
        }
    }

    @ToString
    static class MyBean2 {
        private String beanName;
        @Autowired
        private ApplicationContext applicationContext;

        @PostConstruct
        public void init() {
            beanName = applicationContext.getBeanNamesForType(MyBean2.class)[0];
            log.info("Initializing Bean");
        }
    }

    @Test
    public void test() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(MyBean.class);
        ctx.refresh();
        MyBean bean = ctx.getBean(MyBean.class);
        log.info(bean.toString());
        ctx.close();
    }

    @Test
    public void test2() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(MyBean2.class);
        ctx.refresh();
        MyBean2 bean = ctx.getBean(MyBean2.class);
        log.info(bean.toString());
        ctx.close();
    }

    @Test
    public void test3() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(MyBean2.class);
        ctx.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        ctx.registerBean(CommonAnnotationBeanPostProcessor.class);
        ctx.refresh();
        MyBean2 bean = ctx.getBean(MyBean2.class);
        log.info(bean.toString());
        ctx.close();
    }
}
