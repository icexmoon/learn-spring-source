package cn.icexmoon.demo;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;

import javax.sql.DataSource;

/**
 * @ClassName PostProcessorTests
 * @Description 工厂后处理器用途展示
 * @Author icexmoon@qq.com
 * @Date 2025/6/21 上午9:55
 * @Version 1.0
 */
public class PostProcessorTests {
    static class Bean1{}
    @Configuration
    @ComponentScan(basePackages = "cn.icexmoon.demo.bean")
    @Import(Bean3.class)
    @ImportResource("classpath:bean4.xml")
    static class Config{
        @Bean
        public Bean1 bean1(){
            return new Bean1();
        }
    }

    static class Bean3{}

    @Test
    public void test(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(Config.class);
        ctx.refresh();
        printBeanDefinitionNames(ctx);
        ctx.close();
    }

    @Test
    public void test2(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(Config.class);
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.refresh();
        printBeanDefinitionNames(ctx);
        ctx.close();
    }

    @Configuration
    static class DbConfig{
        @Bean
        public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource){
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            return sqlSessionFactoryBean;
        }

        @Bean
        public DruidDataSource druidDataSource(){
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test");
            druidDataSource.setUsername("root");
            druidDataSource.setPassword("mysql");
            return druidDataSource;
        }
    }

    @Test
    public void test3(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(DbConfig.class);
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(MapperScannerConfigurer.class, db->{
            db.getPropertyValues().add("basePackage", "cn.icexmoon.demo.mapper");
        });
        ctx.refresh();
        printBeanDefinitionNames(ctx);
        ctx.close();
    }

    private static void printBeanDefinitionNames(GenericApplicationContext ctx) {
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }
}
