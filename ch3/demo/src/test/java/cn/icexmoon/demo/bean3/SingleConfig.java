package cn.icexmoon.demo.bean3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SingleConfig
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/21 下午3:04
 * @Version 1.0
 */
@Configuration
public class SingleConfig {
    @Bean
    public Bean1 bean1(){
        return new Bean1();
    }

    @Bean
    public Bean2 bean2(Bean1 bean1){
        return new Bean2(bean1);
    }

    @Bean(initMethod = "init")
    public Bean3 bean3(){
        return new Bean3();
    }
}
