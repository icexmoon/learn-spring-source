package cn.icexmoon.proxy.cglib.fastclass;

import cn.icexmoon.proxy.Target;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName TargetFastClass
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午4:16
 * @Version 1.0
 */
public class TargetFastClass extends FastClass {
    public TargetFastClass() {
        super(Target.class);
    }

    private static final MethodDefinition METHOD_DO_SOMETHING_DEFINITION;
    private static final MethodDefinition METHOD_DO_SOMETHING_ELSE_DEFINITION;
    private static final MethodDefinition METHOD_PLUS_DEFINITION;
    private static final MethodDefinition[] methodDefinitions;

    static {
        METHOD_DO_SOMETHING_DEFINITION = new MethodDefinition(0, "doSomething", new Class[0], new Signature("doSomething", "()V"));
        METHOD_DO_SOMETHING_ELSE_DEFINITION = new MethodDefinition(1, "doSomethingElse", new Class[0], new Signature("doSomethingElse", "()V"));
        METHOD_PLUS_DEFINITION = new MethodDefinition(2, "plus", new Class[]{int.class, int.class}, new Signature("plus", "(II)I"));
        methodDefinitions = new MethodDefinition[]{METHOD_DO_SOMETHING_DEFINITION, METHOD_DO_SOMETHING_ELSE_DEFINITION, METHOD_PLUS_DEFINITION};
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
        if (METHOD_DO_SOMETHING_DEFINITION.getIndex() == index) {
            ((Target) obj).doSomething();
        } else if (METHOD_DO_SOMETHING_ELSE_DEFINITION.getIndex() == index) {
            ((Target) obj).doSomethingElse();
        } else if (METHOD_PLUS_DEFINITION.getIndex() == index) {
            result = ((Target) obj).plus((int) args[0], (int) args[1]);
        } else {
            throw new NoSuchMethodError("index is " + index);
        }
        return result;
    }

    @Override
    public Object newInstance(int index, Object[] args) throws InvocationTargetException {
        // 示例中只有一个构造器
        return new Target();
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
