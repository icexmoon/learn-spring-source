package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
public class FactoryBeanTests {
    @Component("student")
    static class StudentFactoryBean implements FactoryBean<Student> {

        private final Random random = new Random();
        private List<String> names = List.of("Tom", "Jack", "Jane");

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
    @NoArgsConstructor
    static class Student{
        private String name;
        private int age;
    }

    @Configuration
    @Import(StudentFactoryBean.class)
    static class Config{}

    @Test
    public void testFactoryBean() {
        AnnotationConfigApplicationContext context =  new AnnotationConfigApplicationContext(Config.class);
        Student student1 = (Student)context.getBean("student");
        Student student2 = (Student)context.getBean("student");
        System.out.println(student1);
        System.out.println(student2);
        context.close();
    }

    @Test
    public void testFactoryBean2() throws Exception {
        AnnotationConfigApplicationContext context =  new AnnotationConfigApplicationContext(Config.class);
        StudentFactoryBean studentFactoryBean = (StudentFactoryBean)context.getBean("&student");
        Student student1 = studentFactoryBean.getObject();
        Student student2 = studentFactoryBean.getObject();
        System.out.println(student1);
        System.out.println(student2);
        context.close();
    }

    @Test
    public void testFactoryBean3() throws Exception {
        AnnotationConfigApplicationContext context =  new AnnotationConfigApplicationContext(Config.class);
        StudentFactoryBean studentFactoryBean = context.getBean(StudentFactoryBean.class);
        Student student1 = studentFactoryBean.getObject();
        Student student2 = studentFactoryBean.getObject();
        System.out.println(student1);
        System.out.println(student2);
        context.close();
    }

    @Test
    public void testFactoryBean4() {
        AnnotationConfigApplicationContext context =  new AnnotationConfigApplicationContext(Config.class);
        Student student1 = context.getBean(Student.class);
        Student student2 = context.getBean(Student.class);
        System.out.println(student1);
        System.out.println(student2);
        context.close();
    }
}
