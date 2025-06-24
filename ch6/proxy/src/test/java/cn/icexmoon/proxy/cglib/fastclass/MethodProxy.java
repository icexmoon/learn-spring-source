package cn.icexmoon.proxy.cglib.fastclass;

import lombok.Data;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.reflect.FastClass;

/**
 * @ClassName MethodProxy
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午4:45
 * @Version 1.0
 */
@Data
public class MethodProxy {
    @Data
    static class FastClassInfo {
        private FastClass fastClass;
        private int index;
    }

    private FastClassInfo proxyFastClassInfo;
    private FastClassInfo originalFastClassInfo;

    public static MethodProxy create(Class OriginalClass, Class ProxyClass, String desc, String originalMethodName, String superMethodName) {
        // 设置原始类型的 fastClassInfo
        MethodProxy newProxyMethod = new MethodProxy();
        FastClassInfo originalFastClassInfo = new FastClassInfo();
        TargetFastClass targetFastClass = new TargetFastClass();
        originalFastClassInfo.setFastClass(targetFastClass);
        int index = targetFastClass.getIndex(new Signature(originalMethodName, desc));
        originalFastClassInfo.setIndex(index);
        newProxyMethod.setOriginalFastClassInfo(originalFastClassInfo);
        // 设置代理类型的 fastClassInfo
        FastClassInfo proxyFastClassInfo = new FastClassInfo();
        ProxyFastClass proxyFastClass = new ProxyFastClass();
        proxyFastClassInfo.setFastClass(proxyFastClass);
        index = proxyFastClass.getIndex(new Signature(superMethodName, desc));
        proxyFastClassInfo.setIndex(index);
        newProxyMethod.setProxyFastClassInfo(proxyFastClassInfo);
        return newProxyMethod;
    }

    public Object invoke(Object originalObj, Object[] args) throws Throwable {
        return originalFastClassInfo.getFastClass().invoke(originalFastClassInfo.getIndex(), originalObj, args);
    }

    public Object invokeSuper(Object proxyObj, Object[] args) throws Throwable {
        return proxyFastClassInfo.getFastClass().invoke(proxyFastClassInfo.getIndex(), proxyObj, args);
    }
}
