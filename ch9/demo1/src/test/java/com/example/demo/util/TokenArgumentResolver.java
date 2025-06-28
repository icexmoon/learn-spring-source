package com.example.demo.util;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @ClassName TokenParamResolver
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/27 下午6:56
 * @Version 1.0
 */
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Token parameterAnnotation = parameter.getParameterAnnotation(Token.class);
        return parameterAnnotation != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String token = webRequest.getHeader("token");
        if (token != null) {
            return token;
        }
        // 头信息中没有，尝试通过查询参数获取
        token = webRequest.getParameter("token");
        return token;
    }
}
