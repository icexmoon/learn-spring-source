package cn.icexmoon.demo1;

import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Date;

/**
 * @ClassName BeanWrapperImplTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 上午11:34
 * @Version 1.0
 */
public class BeanWrapperImplTests {
    @Setter
    @ToString
    private static class User{
        private String name;
        private int age;
        private Date birthday;
    }

    @Test
    public void testBeanWrapperImpl() {
        User user = new User();
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(user);
        beanWrapper.setPropertyValue("name","Tom");
        beanWrapper.setPropertyValue("age","20");
        beanWrapper.setPropertyValue("birthday","2000/1/1");
        System.out.println(user);
    }
}
