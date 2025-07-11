package com.example.demo;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
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
 * @Description DataSource 自动配置
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 下午4:53
 * @Version 1.0
 */
public class DbTests {
    @Configuration
    @Import(MyImportSelector.class)
    static class Config{}

    static class MyImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    DataSourceAutoConfiguration.class.getName()
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
        context.refresh();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        HikariDataSource dataSource = context.getBean("dataSource", HikariDataSource.class);
        System.out.println(dataSource.getJdbcUrl());
        System.out.println(dataSource.getUsername());
        context.close();
    }
}
