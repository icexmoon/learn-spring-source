package cn.icexmoon.demo1.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @ClassName Employee
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/7 下午12:29
 * @Version 1.0
 */
@Component
@Data
public class Employee {
    private int id;
    private String name;
}
