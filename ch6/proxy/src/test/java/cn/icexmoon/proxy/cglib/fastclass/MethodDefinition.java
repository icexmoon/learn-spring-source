package cn.icexmoon.proxy.cglib.fastclass;


import lombok.Getter;
import org.springframework.cglib.core.Signature;

/**
 * @ClassName MethodDefinition
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午4:16
 * @Version 1.0
 */
public class MethodDefinition {
    @Getter
    private int index;
    private String name;
    private Class[] parameterTypes;
    private Signature signature;

    public MethodDefinition(int index, String name, Class[] parameterTypes, Signature signature) {
        this.index = index;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.signature = signature;
    }

    public boolean equals(String name, Class[] parameterTypes) {
        if (!(name.equals(this.name) && parameterTypes.length == this.parameterTypes.length)) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].equals(this.parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Class[] parameterTypes) {
        if (parameterTypes.length != this.parameterTypes.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].equals(this.parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Signature signature) {
        if (this.signature.equals(signature)){
            return true;
        }
        return false;
    }
}
