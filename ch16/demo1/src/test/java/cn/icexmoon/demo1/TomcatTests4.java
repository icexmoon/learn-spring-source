package cn.icexmoon.demo1;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName TomcatTests
 * @Description Spring 容器与 Tomcat 的整合
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 上午10:50
 * @Version 1.0
 */
public class TomcatTests4 {
    public static class MyServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().println("<h1>Hello World</h1>");
        }
    }

    @Controller
    static class MyController {
        @GetMapping("/hello2")
        @ResponseBody
        public Map<String, String> hello2() {
            return Map.of("msg", "Hello World");
        }
    }

    @Configuration
    @Import(MyController.class)
    static class WebConfig {
        @Bean
        DispatcherServletRegistrationBean dispatcherServletRegistrationBean(){
            return new DispatcherServletRegistrationBean(dispatcherServlet(), "/");
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        public RequestMappingHandlerAdapter handlerAdapter() {
            RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
            requestMappingHandlerAdapter.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
            return requestMappingHandlerAdapter;
        }
    }

    @Test
    public void test() throws IOException, LifecycleException {
        // 创建 Spring 容器
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(WebConfig.class);
        springContext.refresh();
        Tomcat tomcat = new Tomcat();
        // 创建临时目录作为 Tomcat 应用的目录
        File dir = Files.createTempDirectory("tomcat.").toFile();
        dir.deleteOnExit(); // 程序退出后自动删除
        // 添加 context，虚拟路径为根路径/，本地文件路径为临时目录
        Context context = tomcat.addContext("", dir.getAbsolutePath());
        // 添加 Servlet 容器的初始化器
        context.addServletContainerInitializer(new ServletContainerInitializer() {
            @Override
            public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
                ctx.addServlet("myServlet", MyServlet.class).addMapping("/hello");
                // 将 Spring 容器的 DispatcherServlet 添加到 Tomcat
                for (DispatcherServletRegistrationBean registrationBean : springContext.getBeansOfType(DispatcherServletRegistrationBean.class).values()) {
                    registrationBean.onStartup(ctx);
                }
            }
        }, Collections.emptySet());
        // 启动 Tomcat
        tomcat.start();
        // 设置连接，监听 8080 端口
        Connector connector = new Connector(new Http11Nio2Protocol());
        connector.setPort(8080);
        tomcat.setConnector(connector);
        tomcat.getServer().await();
    }
}
