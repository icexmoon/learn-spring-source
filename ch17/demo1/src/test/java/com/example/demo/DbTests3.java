package com.example.demo;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @ClassName DbTests
 * @Description 事务管理器自动配置
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 下午4:53
 * @Version 1.0
 */
public class DbTests3 {
    @Configuration
    @Import(MyImportSelector.class)
    static class Config {
    }

    static class MyImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    DataSourceAutoConfiguration.class.getName(),
                    MybatisAutoConfiguration.class.getName(),
                    TransactionAutoConfiguration.class.getName(),
                    DataSourceTransactionManagerAutoConfiguration.class.getName()
            };
        }
    }


    @Test
    public void test() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConfigurableEnvironment environment = context.getEnvironment();
        environment.getPropertySources().addLast(new MapPropertySource("dataSource", Map.of(
                "spring.datasource.url", "jdbc:mysql://localhost:3306/test",
                "spring.datasource.username", "root",
                "spring.datasource.password", "mysql"
        )));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.registerBean(Config.class);
        AutoConfigurationPackages.register(context, this.getClass().getPackageName());
        context.refresh();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            String resourceDescription = context.getBeanDefinition(beanDefinitionName).getResourceDescription();
            if (resourceDescription != null)
                System.out.printf("名称：%s，来源：%s%n", beanDefinitionName, resourceDescription);
        }
        context.close();
    }
}
