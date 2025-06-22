package cn.icexmoon.demo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @ClassName ScopeTests
 * @Description 演示作用域失效的情况，使用 @Lazy 注解
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午3:50
 * @Version 1.0
 */
@Slf4j
@SpringJUnitConfig(classes = {ScopeTests3.SingletonBean.class, ScopeTests3.PrototypeBean.class})
public class ScopeTests3 {
    @Component
    static class SingletonBean{
        @Getter
        @Autowired
        private PrototypeBean prototypeBean;
    }
    @Component
    @Scope(value = AbstractBeanDefinition.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    static class PrototypeBean{}

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testPrototypeBean() {
        SingletonBean bean = applicationContext.getBean(SingletonBean.class);
        PrototypeBean prototypeBean1 = bean.getPrototypeBean();
        PrototypeBean prototypeBean2 = bean.getPrototypeBean();
        PrototypeBean prototypeBean3 = bean.getPrototypeBean();
        log.info(prototypeBean1.toString());
        log.info(prototypeBean2.toString());
        log.info(prototypeBean3.toString());
        Assertions.assertSame(prototypeBean1, prototypeBean2);
        Assertions.assertSame(prototypeBean2, prototypeBean3);
        log.info(prototypeBean1.getClass().toString());
    }
}
