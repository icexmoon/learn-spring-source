package cn.icexmoon.demo;

import cn.icexmoon.demo.bean3.SingleConfig;
import cn.icexmoon.demo.mapper.Mapper1;
import cn.icexmoon.demo.mapper.Mapper2;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Set;

/**
 * @ClassName PostProcessorWorkerTests
 * @Description 工厂后处理器的工作原理
 * @Author icexmoon@qq.com
 * @Date 2025/6/21 下午12:06
 * @Version 1.0
 */
@Slf4j
public class PostProcessorWorkerTests {
    @Configuration
    @ComponentScan(basePackages = "cn.icexmoon.demo.bean2")
    static class Config {
    }

    static class ComponentScanPostProcessor implements BeanFactoryPostProcessor {

        @SneakyThrows
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            // 查找具有 @Configuration 注解的 配置类
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                String beanClassName = beanDefinition.getBeanClassName();
                Class<?> beanClass = Class.forName(beanClassName);
                // 判断类型是否有 @component 注解 和 @componentScan 注解
                Configuration configuration = AnnotationUtils.findAnnotation(beanClass, Configuration.class);
                ComponentScan componentScan = AnnotationUtils.findAnnotation(beanClass, ComponentScan.class);
                if (configuration != null && componentScan != null) {
                    // 同时具有 @Component 注解和 @ComponentScan 注解
                    log.info("找到匹配的类" + beanClassName);
                    // 获取要处理的包
                    String[] basePackages = componentScan.basePackages();
                    for (String basePackage : basePackages) {
                        // 获取包对应的类路径，比如 classpath*:cn/icexmoon/demo/bean2/**/*.class
                        String classpath = "classpath*:%s/**/*.class".formatted(basePackage.replace('.', '/'));
                        log.info("classpath:" + classpath);
                        // 加载字节码文件（代码运行时没有源码，只有字节码）
                        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(classpath);
                        CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
                        for (Resource resource : resources) {
                            // 要处理的类对应的字节码
                            log.info("class file:" + resource.getFilename());
                            // 读取类的元信息
                            MetadataReader metadataReader = cachingMetadataReaderFactory.getMetadataReader(resource);
                            // 判断类是否有 @Component 注解或其子注解
                            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                            if (annotationMetadata.hasAnnotation(Component.class.getName())
                                    || annotationMetadata.hasMetaAnnotation(Component.class.getName())) {
                                // 获取要处理的字节码的类名
                                String className = metadataReader.getClassMetadata().getClassName();
                                // 添加类定义
                                // 方便起见，这里默认容器类型为 GenericApplicationContext
                                if (beanFactory instanceof DefaultListableBeanFactory defaultListableBeanFactory) {
                                    log.info("类[%s]被添加到容器中".formatted(className));
                                    AbstractBeanDefinition bb = BeanDefinitionBuilder.genericBeanDefinition(className)
                                            .setScope("singleton")
                                            .getBeanDefinition();
                                    AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
                                    defaultListableBeanFactory.registerBeanDefinition(generator.generateBeanName(bb, defaultListableBeanFactory), bb);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static class BeanMethodPostProcessor implements BeanFactoryPostProcessor {
        @SneakyThrows
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                String beanClassName = beanDefinition.getBeanClassName();
                // 处理带 @Configuration 注解的 bean 定义
                Class<?> beanClass = Class.forName(beanClassName);
                Configuration configuration = AnnotationUtils.findAnnotation(beanClass, Configuration.class);
                if (configuration != null) {
                    // 获取 bean 方法
                    CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
                    // 简单起见，这里默认所有配置类都是单独的配置类（非内部类）
                    String classpath = beanClassName.replace('.', '/') + ".class";
                    Resource resource = new PathMatchingResourcePatternResolver().getResource("classpath:" + classpath);
                    MetadataReader metadataReader = cachingMetadataReaderFactory.getMetadataReader(resource);
                    Set<MethodMetadata> annotatedMethods = metadataReader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
                    for (MethodMetadata annotatedMethod : annotatedMethods) {
                        log.info(annotatedMethod.getMethodName());
                        // 构造 bean 定义
                        // 将 bean 方法用于 bean 创建的工厂方法
                        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition()
                                // 处理工厂方法参数注入
                                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR)
                                .setFactoryMethodOnBean(annotatedMethod.getMethodName(), beanDefinitionName);
                        // 如果 bean 注解设置了 init 方法
                        String initMethod = (String) annotatedMethod.getAnnotationAttributes(Bean.class.getName()).get("initMethod");
                        if (initMethod != null && !initMethod.isEmpty()) {
                            beanDefinitionBuilder.setInitMethodName(initMethod);
                        }
                        AbstractBeanDefinition bd = beanDefinitionBuilder
                                .getBeanDefinition();
                        if (beanFactory instanceof DefaultListableBeanFactory defaultListableBeanFactory) {
                            defaultListableBeanFactory.registerBeanDefinition(annotatedMethod.getMethodName(), bd);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void test() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(Config.class);
        context.registerBean(ComponentScanPostProcessor.class);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
        context.close();
    }

    @Test
    public void test2() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(SingleConfig.class);
        context.registerBean(BeanMethodPostProcessor.class);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
        context.close();
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

        @Bean
        public MapperFactoryBean<Mapper1> mapper1(SqlSessionFactory sqlSessionFactory){
            MapperFactoryBean<Mapper1> mapperFactoryBean = new MapperFactoryBean<>(Mapper1.class);
            mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
            return mapperFactoryBean;
        }

        @Bean
        public MapperFactoryBean<Mapper2> mapper2(SqlSessionFactory sqlSessionFactory){
            MapperFactoryBean<Mapper2> mapperFactoryBean = new MapperFactoryBean<>(Mapper2.class);
            mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
            return mapperFactoryBean;
        }
    }

    @Configuration
    static class DbConfig2{
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
    public void test3() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(DbConfig.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
        context.close();
    }

    @Test
    public void test4() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(DbConfig2.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(MapperScanPostProcessor.class, bd -> {
            bd.getPropertyValues().add("basePackage", "cn.icexmoon.demo.mapper");
        });
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
        context.close();
    }

    static class MapperScanPostProcessor implements BeanDefinitionRegistryPostProcessor {
        // Mapper 所在包
        @Setter
        private String basePackage;

        @Override
        @SneakyThrows
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            // 在 bean 工厂加载了所有 bean 定义后调用
            // 扫描并加载 Mapper
            if (basePackage == null || basePackage.isEmpty()){
                return;
            }
            // 从包路径扫描并加载字节码
            String locationPatter = "classpath*:%s/**/*.class".formatted(basePackage.replace('.', '/'));
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(locationPatter);
            CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
            for (Resource resource : resources) {
                // 获取元信息
                MetadataReader metadataReader = cachingMetadataReaderFactory.getMetadataReader(resource);
                // 只处理接口
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                if(classMetadata.isInterface()){
                    // 构造 bean 定义
                    AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class)
                            .addConstructorArgValue(classMetadata.getClassName())
                            .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                            .getBeanDefinition();
                    // 生成名称
                    AbstractBeanDefinition beanNameDefinition = BeanDefinitionBuilder.genericBeanDefinition(classMetadata.getClassName())
                            .getBeanDefinition();
                    AnnotationBeanNameGenerator annotationBeanNameGenerator = new AnnotationBeanNameGenerator();
                    String beanName = annotationBeanNameGenerator.generateBeanName(beanNameDefinition, registry);
                    registry.registerBeanDefinition(beanName, beanDefinition);
                }
            }
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            BeanDefinitionRegistryPostProcessor.super.postProcessBeanFactory(beanFactory);
        }
    }
}
