package com.example.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @ClassName ResultOkReturnValueResolver
 * @Description 用于处理 @ResultOk 的返回值解析器
 * @Author icexmoon@qq.com
 * @Date 2025/6/28 上午10:37
 * @Version 1.0
 */
public class ResultOkReturnValueResolver implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 有 @ResultOk 注解才处理
        boolean updated = returnType.hasMethodAnnotation(ResultOk.class);
        return updated;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 将返回值包装成标准返回
        StandardResult<?> standardResult = null;
        if (returnValue == null) {
            standardResult = StandardResult.success();
        } else {
            standardResult = StandardResult.success(returnValue);
        }
        // JSON 后写入返回报文的输出流
        ObjectMapper mapper = new ObjectMapper();
        String resultStr = mapper.writeValueAsString(standardResult);
        HttpServletResponse httpServletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(resultStr);
        // 告诉 mvc 请求已经处理完毕，无需再处理
        mavContainer.setRequestHandled(true);
    }
}
