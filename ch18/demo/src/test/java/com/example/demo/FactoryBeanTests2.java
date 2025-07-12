package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * @ClassName FactoryBeanTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/12 上午10:19
 * @Version 1.0
 */
public class FactoryBeanTests2 {
    @Component("student")
    static class StudentFactoryBean implements FactoryBean<Student> {

        private final Random random = new Random();
        private final List<String> names = List.of("Tom", "Jack", "Jane");

        @Override
        public Student getObject() throws Exception {
            int age = random.nextInt(100);
            int nameIndex = random.nextInt(names.size());
            return new Student(names.get(nameIndex), age);
        }

        @Override
        public Class<?> getObjectType() {
            return Student.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    static class ClassRoom{
        private int i;
    }

    @Data
    @NoArgsConstructor
    @Slf4j
    static class Student implements ApplicationContextAware {
        private String name;
        private int age;

        @Autowired
        public void setClassRoom(ClassRoom classRoom) {
            log.info("Student setClassRoom");
            this.classRoom = classRoom;
        }

        private ClassRoom classRoom;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
            log.info("Student created");
        }

        @PostConstruct
        public void init() {
            log.info("Student init");
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            log.info("Student setApplicationContext");
        }
    }

    @Configuration
    @Import(StudentFactoryBean.class)
    static class Config {
        @Bean
        public ClassRoom classRoom() {
            return new ClassRoom(1);
        }
    }

    @Test
    public void testFactoryBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        Student student1 = (Student) context.getBean("student");
        Student student2 = (Student) context.getBean("student");
        System.out.println(student1);
        System.out.println(student2);
        context.close();
    }
}