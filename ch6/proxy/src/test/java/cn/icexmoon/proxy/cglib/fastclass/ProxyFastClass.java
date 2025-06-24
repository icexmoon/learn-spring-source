package cn.icexmoon.proxy.cglib.fastclass;

import cn.icexmoon.proxy.cglib.$Proxy2;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName ProxyFastClass
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午4:58
 * @Version 1.0
 */
public class ProxyFastClass extends FastClass {
    private static MethodDefinition METHOD_DO_SOMETHING_DEFINITION;
    private static MethodDefinition METHOD_DO_SOMETHING_ELSE_DEFINITION;
    private static MethodDefinition METHOD_PLUS_DEFINITION;
    private static MethodDefinition[] methodDefinitions;

    static {
        METHOD_DO_SOMETHING_DEFINITION = new MethodDefinition(0, "doSomethingSuper", new Class[0], new Signature("doSomethingSuper", "()V"));
        METHOD_DO_SOMETHING_ELSE_DEFINITION = new MethodDefinition(1, "doSomethingElseSuper", new Class[0], new Signature("doSomethingElseSuper", "()V"));
        METHOD_PLUS_DEFINITION = new MethodDefinition(2, "plus", new Class[]{int.class, int.class}, new Signature("plusSuper", "(II)I"));
        methodDefinitions = new MethodDefinition[]{METHOD_DO_SOMETHING_DEFINITION, METHOD_DO_SOMETHING_ELSE_DEFINITION, METHOD_PLUS_DEFINITION};
    }

    public ProxyFastClass() {
        super($Proxy2.class);
    }

    @Override
    public int getIndex(String name, Class[] parameterTypes) {
        for (MethodDefinition methodDefinition : methodDefinitions) {
            if (methodDefinition.equals(name, parameterTypes)) {
                return methodDefinition.getIndex();
            }
        }
        return -1;
    }

    @Override
    public int getIndex(Class[] parameterTypes) {
        for (MethodDefinition methodDefinition : methodDefinitions) {
            if (methodDefinition.equals(parameterTypes)) {
                return methodDefinition.getIndex();
            }
        }
        return -1;
    }

    @Override
    public Object invoke(int index, Object obj, Object[] args) throws InvocationTargetException {
        Object result = null;
        if (index == METHOD_DO_SOMETHING_DEFINITION.getIndex()) {
            (($Proxy2) obj).doSomethingSuper();
        } else if (index == METHOD_DO_SOMETHING_ELSE_DEFINITION.getIndex()) {
            (($Proxy2) obj).doSomethingElseSuper();
        } else if (index == METHOD_PLUS_DEFINITION.getIndex()) {
            result = (($Proxy2) obj).plusSuper((int) args[0], (int) args[1]);
        } else {
            throw new NoSuchMethodError("index is " + index);
        }
        return result;
    }

    @Override
    public Object newInstance(int index, Object[] args) throws InvocationTargetException {
        return new $Proxy2(null);
    }

    @Override
    public int getIndex(Signature sig) {
        for (MethodDefinition methodDefinition : methodDefinitions) {
            if (methodDefinition.equals(sig)) {
                return methodDefinition.getIndex();
            }
        }
        return -1;
    }

    @Override
    public int getMaxIndex() {
        return methodDefinitions.length - 1;
    }
}
