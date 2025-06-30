package cn.icexmoon.demo1;

import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.util.Date;

/**
 * @ClassName DataBinderTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 上午11:49
 * @Version 1.0
 */
public class DataBinderTests3 {
    @ToString
    private static class User{
        private String name;
        private int age;
        private Date birthday;
    }

    @Test
    public void test() {
        User user = new User();
        DataBinder dataBinder = new DataBinder(user);
        dataBinder.initDirectFieldAccess();
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", "Tom");
        pvs.add("age", "20");
        pvs.add("birthday", "2000/1/1");
        dataBinder.bind(pvs);
        System.out.println(user);
    }
}
