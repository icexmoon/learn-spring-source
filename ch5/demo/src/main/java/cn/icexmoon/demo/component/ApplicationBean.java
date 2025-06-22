package cn.icexmoon.demo.component;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * @ClassName ApplicationBean
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午2:49
 * @Version 1.0
 */
@Scope(WebApplicationContext.SCOPE_APPLICATION)
@Component
@Slf4j
public class ApplicationBean {
    @PreDestroy
    public void destroy() {
        log.info("destroy");
    }
}
