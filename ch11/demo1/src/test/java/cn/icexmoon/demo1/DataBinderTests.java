package cn.icexmoon.demo1;

import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;

import java.util.Date;

/**
 * @ClassName DataBinderTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 上午11:49
 * @Version 1.0
 */
public class DataBinderTests {
    @Setter
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
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", "Tom");
        pvs.add("age", "20");
        pvs.add("birthday", "2000/1/1");
        dataBinder.bind(pvs);
        System.out.println(user);
    }

    @Test
    public void test2() {
        User user = new User();
        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(user, "user");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000/1/1");
        ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request);
        dataBinder.bind(pvs);
        System.out.println(user);
    }

    @Test
    public void test3() {
        User user = new User();
        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(user, "user");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000/1/1");
        dataBinder.bind(request);
        System.out.println(user);
    }
}
