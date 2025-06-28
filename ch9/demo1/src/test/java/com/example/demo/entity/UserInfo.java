package com.example.demo.entity;

import lombok.Data;

/**
 * @ClassName UserInfo
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/28 上午10:26
 * @Version 1.0
 */
@Data
public class UserInfo {
    private String name;
    private int age;

    public UserInfo(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
