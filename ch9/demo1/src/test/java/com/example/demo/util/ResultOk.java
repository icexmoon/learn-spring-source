package com.example.demo.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName Result
 * @Description 对控制器返回值进行包装，包装为标准返回格式
 * @Author icexmoon@qq.com
 * @Date 2025/6/28 上午10:24
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultOk {
}
