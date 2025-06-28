package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName User
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/28 下午5:58
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@ToString
public class User {
    private String name;
    private int age;
}
