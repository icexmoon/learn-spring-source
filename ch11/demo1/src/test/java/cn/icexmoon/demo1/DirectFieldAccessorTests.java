package cn.icexmoon.demo1;

import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.DirectFieldAccessor;

import java.util.Date;

/**
 * @ClassName DirectFieldAccessorTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 上午11:42
 * @Version 1.0
 */
public class DirectFieldAccessorTests {
    @ToString
    private static class User{
        private String name;
        private int age;
        private Date birthday;
    }

    @Test
    public void testDirectFieldAccessor() {
        User user = new User();
        DirectFieldAccessor dfa = new DirectFieldAccessor(user);
        dfa.setPropertyValue("name","Tom");
        dfa.setPropertyValue("age","18");
        dfa.setPropertyValue("birthday","2000/1/1");
        System.out.println(user);
    }
}
