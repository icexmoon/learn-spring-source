package cn.icexmoon.demo1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.SimpleTypeConverter;

import java.util.Date;

/**
 * @ClassName SImpleConverterTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 上午11:25
 * @Version 1.0
 */
public class SimpleConverterTests {
    @Test
    public void testSimpleConverter() {
        SimpleTypeConverter converter = new SimpleTypeConverter();
        Integer intVal = converter.convertIfNecessary("10", Integer.class);
        Date date = converter.convertIfNecessary("2025/06/30", Date.class);
        System.out.println(intVal);
        System.out.println(date);
    }
}
