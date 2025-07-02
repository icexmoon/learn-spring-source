package cn.icexmoon.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @ClassName MessageConverterTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/2 下午4:28
 * @Version 1.0
 */
public class MessageConverterTests {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class User {
        private String name;
        private int age;
    }

    @Test
    public void testObject2JsonMessage() throws IOException {
        // 将对象转换为 JSON 格式的消息
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        if (converter.canWrite(User.class, MediaType.APPLICATION_JSON)) {
            converter.write(new User("Tom", 20), MediaType.APPLICATION_JSON, mockHttpOutputMessage);
            String body = mockHttpOutputMessage.getBodyAsString();
            System.out.println(body);
        }
    }

    @Test
    public void testObject2XmlMessage() throws IOException {
        // 将对象转换为 JSON 格式的消息
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
        if (converter.canWrite(User.class, MediaType.APPLICATION_XML)) {
            converter.write(new User("Tom", 20), MediaType.APPLICATION_XML, mockHttpOutputMessage);
            String body = mockHttpOutputMessage.getBodyAsString();
            System.out.println(body);
        }
    }

    @Test
    public void testJsonMessage2Object() throws IOException {
        // 模拟输入消息
        MockHttpInputMessage mockHttpInputMessage = new MockHttpInputMessage("""
                {"name":"LiLei","age":11}
                """
                .getBytes(StandardCharsets.UTF_8));
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 判断能否转换消息
        if (converter.canRead(User.class, MediaType.APPLICATION_JSON)) {
            // 转换消息
            Object user = converter.read(User.class, mockHttpInputMessage);
            System.out.println(user);
        }
    }

    @Test
    public void testXmlMessage2Object() throws IOException {
        // 模拟输入消息
        MockHttpInputMessage mockHttpInputMessage = new MockHttpInputMessage("""
                <User><name>Tom</name><age>20</age></User>
                """
                .getBytes(StandardCharsets.UTF_8));
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
        // 判断能否转换消息
        if (converter.canRead(User.class, MediaType.APPLICATION_XML)) {
            // 转换消息
            Object user = converter.read(User.class, mockHttpInputMessage);
            System.out.println(user);
        }
    }

    static class TestController {
        @ResponseBody
        public User test() {
            return new User("Tom", 15);
        }
    }

    @Test
    public void testMultiMessageConverters() throws IOException, NoSuchMethodException, HttpMediaTypeNotAcceptableException {
        // 存在多个消息转换器
        RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(
                List.of(new MappingJackson2XmlHttpMessageConverter(), new MappingJackson2HttpMessageConverter())
        );
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("test"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        NativeWebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest(), response);
        // 处理控制器方法返回值
        requestResponseBodyMethodProcessor.handleReturnValue(new User("Tom", 21),
                handlerMethod.getReturnType(), new ModelAndViewContainer(), webRequest);
        String content = response.getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testMultiMessageConverters2() throws IOException, NoSuchMethodException, HttpMediaTypeNotAcceptableException {
        // 存在多个消息转换器
        RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(
                List.of(new MappingJackson2HttpMessageConverter(), new MappingJackson2XmlHttpMessageConverter())
        );
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("test"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        NativeWebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest(), response);
        // 处理控制器方法返回值
        requestResponseBodyMethodProcessor.handleReturnValue(new User("Tom", 21),
                handlerMethod.getReturnType(), new ModelAndViewContainer(), webRequest);
        String content = response.getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testMultiMessageConverters3() throws IOException, NoSuchMethodException, HttpMediaTypeNotAcceptableException {
        // 存在多个消息转换器
        RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(
                List.of(new MappingJackson2HttpMessageConverter(), new MappingJackson2XmlHttpMessageConverter())
        );
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("test"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 由客户端指定返回报文格式
        request.addHeader("Accept", "text/xml");
        NativeWebRequest webRequest = new ServletWebRequest(request, response);
        // 处理控制器方法返回值
        requestResponseBodyMethodProcessor.handleReturnValue(new User("Tom", 21),
                handlerMethod.getReturnType(), new ModelAndViewContainer(), webRequest);
        String content = response.getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testMultiMessageConverters4() throws IOException, NoSuchMethodException, HttpMediaTypeNotAcceptableException {
        // 存在多个消息转换器
        RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(
                List.of(new MappingJackson2HttpMessageConverter(), new MappingJackson2XmlHttpMessageConverter())
        );
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("test"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 由客户端指定返回报文格式
        request.addHeader("Accept", "text/xml");
        // 由服务端强制指定响应报文格式
        response.setContentType("application/json;charset=utf-8");
        NativeWebRequest webRequest = new ServletWebRequest(request, response);
        // 处理控制器方法返回值
        requestResponseBodyMethodProcessor.handleReturnValue(new User("Tom", 21),
                handlerMethod.getReturnType(), new ModelAndViewContainer(), webRequest);
        String content = response.getContentAsString();
        System.out.println(content);
    }
}
