package cn.icexmoon.demo;

/**
 * @ClassName User
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/29 下午2:40
 * @Version 1.0
 */
public class User {
    private String name;
    private int age;
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }
}
