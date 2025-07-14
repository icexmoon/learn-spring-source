package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @ClassName AutowiredTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/13 上午10:36
 * @Version 1.0
 */
public class AutowiredTests {
    @Component
    @Data
    static class Student {
        @Lazy
        @Autowired
        private Teacher teacher;
        private ClassRoom classRoom;

        @Autowired
        public void setClassRoom(ClassRoom classRoom) {
            this.classRoom = classRoom;
        }

        @Autowired
        private Optional<Teacher> optionalTeacher;

        @Autowired
        @ToString.Exclude
        private ObjectFactory<Teacher> objectFactory;

        @Autowired
        @ToString.Exclude
        private Service[] services;

        @Autowired
        @ToString.Exclude
        private List<Service> serviceList;

        @Autowired
        @ToString.Exclude
        private ConfigurableApplicationContext applicationContext;

        @Autowired
        private Dao<User> userDao;

        @Autowired
        @Qualifier("service1")
        private Service service1;

//        @Autowired
//        private Service primaryService;

        @Autowired
        private Service service2;
    }

    interface Service {
    }

    @Data
    @AllArgsConstructor
    static class Teacher {
        private String name;
        private int age;
    }

    @Data
    @AllArgsConstructor
    static class ClassRoom {
        private int id;
    }

    interface Dao<T> {
    }

    static class User {
    }

    static class Employee {
    }

    static class Police {
    }

    @Configuration
    @Import({Student.class})
    static class Config {
        @Bean
        public Teacher teacher() {
            return new Teacher("Tom", 20);
        }

        @Bean
        public ClassRoom classRoom() {
            return new ClassRoom(1);
        }

        @Bean
        public Service service1() {
            return new Service() {
            };
        }

//        @Primary
        @Bean
        public Service service2() {
            return new Service() {
            };
        }

        @Bean
        public Service service3() {
            return new Service() {
            };
        }

        @Bean
        public Dao<User> userDao() {
            return new Dao<User>() {
            };
        }

        @Bean
        public Dao<Employee> employeeDao() {
            return new Dao<Employee>() {
            };
        }

        @Bean
        public Dao<Police> policeDao() {
            return new Dao<Police>() {
            };
        }
    }

    /**
     * 自定义的处理 @Autowired 注解的 Bean 后处理器
     */
    @Slf4j
    static class AutowiredBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
        private ApplicationContext applicationContext;

        @SneakyThrows
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            for (Field declaredField : bean.getClass().getDeclaredFields()) {
                // 检查是否有 @Autowired 注解
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(declaredField, true);
                    Object dependency = null;
                    Class<?> dependencyType = dependencyDescriptor.getDependencyType();
                    // 检查是不是特殊类型
                    if ((dependency = doResolveAsSpecialBean(dependencyType, beanFactory)) != null) {
                        ;
                    } else if ((dependency = doResolveAsQualifier(declaredField, beanFactory, dependencyType, dependencyDescriptor)) != null) {
                        ;
                    } else if ((dependency = doResolveAsArray(dependencyType, beanFactory)) != null) {
                        ;
                    } else if ((dependency = doResolveAsList(dependencyType, dependencyDescriptor, beanFactory)) != null) {
                        ;
                    } else if ((dependency = doResolveAsLazy(declaredField, beanFactory, dependencyDescriptor)) != null) {
                        ;
                    } else if ((dependency = doResolveAsWrapClass(dependencyType, beanFactory, dependencyDescriptor)) != null) {
                        ;
                    } else if ((dependency = doResolveAsGenericInterface(beanFactory, dependencyType, dependencyDescriptor)) != null) {
                        ;
                    } else if ((dependency = doResolveAsPrimaryBean(dependencyType, beanFactory, dependencyDescriptor)) != null) {
                        ;
                    } else if ((dependency = doResolveAsBeanName(declaredField, dependencyType, beanFactory, dependencyDescriptor)) != null) {
                        ;
                    } else {
                        // 作为依赖注入进行解析，注入原始对象
                        dependency = beanFactory.resolveDependency(dependencyDescriptor, null);
                    }
                    declaredField.setAccessible(true);
                    declaredField.set(bean, dependency);
                }
            }
            for (Method method : bean.getClass().getDeclaredMethods()) {
                // 检查方法是否有 @Autowired 注解
                if (method.isAnnotationPresent(Autowired.class)) {
                    int parameterCount = method.getParameterCount();
                    Object[] args = new Object[parameterCount];
                    for (int i = 0; i < parameterCount; i++) {
                        args[i] = beanFactory.resolveDependency(new DependencyDescriptor(new MethodParameter(method, i), true), null);
                    }
                    method.setAccessible(true);
                    method.invoke(bean, args);
                }
            }
            return bean;
        }

        private Object doResolveAsBeanName(Field declaredField, Class<?> dependencyType, DefaultListableBeanFactory beanFactory, DependencyDescriptor dependencyDescriptor) {
            Object dependency = null;
            // 存在多个候选项时，按照 Bean 名称进行匹配
            for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, dependencyType)) {
                if(name.equals(dependencyDescriptor.getDependencyName())) {
                    dependency = beanFactory.getBean(name);
                    log.info("按照名称进行匹配：{}", dependency);
                    return dependency;
                }
            }
            return dependency;
        }

        private Object doResolveAsPrimaryBean(Class<?> dependencyType, DefaultListableBeanFactory beanFactory, DependencyDescriptor dependencyDescriptor) {
            // 依赖类型有多个候选项，选取 @Primary 注解的进行匹配
            Object dependency = null;
            for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, dependencyType)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
                if (beanDefinition.isPrimary()) {
                    dependency = beanFactory.getBean(name);
                    log.info("按照 @Primary 注解进行匹配：{}", dependency);
                    return dependency;
                }
            }
            return dependency;
        }

        private static Object doResolveAsGenericInterface(DefaultListableBeanFactory beanFactory, Class<?> dependencyType, DependencyDescriptor dependencyDescriptor) {
            Object dependency = null;
            if (dependencyType.isInterface() && dependencyType.getTypeParameters().length > 0) {
                // 依赖类型是泛型接口
                String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, dependencyType);
                ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
                for (String name : beanNames) {
                    // 获取实现了接口的 Bean 定义
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
                    // 判断 Bean 定义与依赖类型是否匹配
                    boolean autowireCandidate = resolver.isAutowireCandidate(new BeanDefinitionHolder(beanDefinition, name), dependencyDescriptor);
                    if (autowireCandidate) {
                        dependency = beanFactory.getBean(name);
                        log.info("泛型接口匹配到 Bean：{}", dependency);
                        return dependency;
                    }
                }
            }
            return dependency;
        }

        private static Object doResolveAsWrapClass(Class<?> dependencyType, DefaultListableBeanFactory beanFactory, DependencyDescriptor dependencyDescriptor) {
            Object dependency = null;
            if (dependencyType == Optional.class || dependencyType == ObjectFactory.class) {
                // 包装类型
                dependency = beanFactory.resolveDependency(dependencyDescriptor, null);
                return dependency;
            }
            return dependency;
        }

        private static Object doResolveAsLazy(Field declaredField, DefaultListableBeanFactory beanFactory, DependencyDescriptor dependencyDescriptor) {
            Object dependency = null;
            if (declaredField.isAnnotationPresent(Lazy.class)) {
                // 有 @Lazy 注解，注入代理对象
                ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
                resolver.setBeanFactory(beanFactory);
                dependency = resolver.getLazyResolutionProxyIfNecessary(dependencyDescriptor, null);
                return dependency;
            }
            return dependency;
        }

        private static Object doResolveAsList(Class<?> dependencyType, DependencyDescriptor dependencyDescriptor, DefaultListableBeanFactory beanFactory) {
            Object dependency = null;
            if (dependencyType == List.class) {
                // 处理 List 依赖注入
                // 获取 List 中的泛型
                Class<?> resolve = dependencyDescriptor.getResolvableType().getGeneric().resolve();
                log.info("List 泛型的类型是 {}", resolve.getName());
                String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, resolve);
                log.info("获取到的 List 元素的 bean 名称：{}", Arrays.toString(beanNames));
                List<Object> beans = new ArrayList<>();
                for (String name : beanNames) {
                    beans.add(beanFactory.getBean(name));
                }
                log.info("生成要注入的 List:{}", beans);
                dependency = beans;
                return dependency;
            }
            return dependency;
        }

        private static Object doResolveAsArray(Class<?> dependencyType, DefaultListableBeanFactory beanFactory) {
            Object dependency = null;
            if (dependencyType.isArray()) {
                // 处理数组依赖注入
                // 获取数组元素类型
                Class<?> componentType = dependencyType.getComponentType();
                String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, componentType);
                List<Object> beans = new ArrayList<>();
                for (String elementBeanName : beanNames) {
                    beans.add(beanFactory.getBean(elementBeanName));
                }
                // 类型转换
                dependency = beanFactory.getTypeConverter().convertIfNecessary(beans, dependencyType);
                return dependency;
            }
            return dependency;
        }

        private static Object doResolveAsQualifier(Field declaredField, DefaultListableBeanFactory beanFactory, Class<?> dependencyType, DependencyDescriptor dependencyDescriptor) {
            Object dependency = null;
            if (declaredField.isAnnotationPresent(Qualifier.class)) {
                // 使用了 @Qualifier 注解限定的字段
                // 获取所有依赖注入类型的 Bean
                String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, dependencyType);
                ContextAnnotationAutowireCandidateResolver candidateResolver = new ContextAnnotationAutowireCandidateResolver();
                for (String name : beanNames) {
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
                    boolean autowireCandidate = candidateResolver.isAutowireCandidate(new BeanDefinitionHolder(beanDefinition, name), dependencyDescriptor);
                    if (autowireCandidate) {
                        dependency = beanFactory.getBean(name);
                        log.info("@Qualifier 注解进行的依赖注入，匹配到：{}", dependency);
                        return dependency;
                    }
                }
            }
            return dependency;
        }

        private Object doResolveAsSpecialBean(Class<?> dependencyType, DefaultListableBeanFactory beanFactory) throws NoSuchFieldException, IllegalAccessException {
            Field resolvableDependencies = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
            resolvableDependencies.setAccessible(true);
            Map<Class<?>, Object> specialBeans = (Map<Class<?>, Object>) resolvableDependencies.get(beanFactory);
            for (Map.Entry<Class<?>, Object> entry : specialBeans.entrySet()) {
                Class<?> clazz = entry.getKey();
                Object bean = entry.getValue();
                if (clazz.isAssignableFrom(dependencyType) && dependencyType.isAssignableFrom(bean.getClass())) {
                    log.info("匹配到特殊 Bean: {}", bean);
                    return bean;
                }
            }
            return null;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }

    @Test
    public void testAutowired() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(AutowiredBeanPostProcessor.class);
        context.refresh();
        Student bean = context.getBean(Student.class);
        System.out.println(bean);
        Teacher teacher = bean.getObjectFactory().getObject();
        System.out.println(teacher);
        Teacher teacher1 = bean.getTeacher();
        System.out.println(teacher1.getClass());
        Service[] services = bean.getServices();
        if (services != null) {
            System.out.println("打印数组");
            for (Service service : services) {
                System.out.println(service);
            }
        }
        List<Service> serviceList = bean.getServiceList();
        if (serviceList != null) {
            System.out.println("打印 List");
            for (Service service : serviceList) {
                System.out.println(service);
            }
        }
        ConfigurableApplicationContext applicationContext = bean.getApplicationContext();
        System.out.println(applicationContext);
        Dao<User> userDao = bean.getUserDao();
        System.out.println(userDao);
        Service service1 = bean.getService1();
        System.out.println(service1);
        context.close();
    }
}
