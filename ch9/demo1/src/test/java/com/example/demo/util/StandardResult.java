package com.example.demo.util;

import lombok.Getter;

/**
 * @ClassName StandardResult
 * @Description 标准返回
 * @Author icexmoon@qq.com
 * @Date 2025/6/28 上午10:28
 * @Version 1.0
 */
@Getter
public class StandardResult<T> {
    private final boolean success;
    private final String msg;
    private final T data;

    private StandardResult(boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public static  <D> StandardResult<D> success(D data, String msg) {
        return new StandardResult<D>(true, msg, data);
    }

    public static <D> StandardResult<D> success(D data) {
        return success(data, "");
    }

    public static StandardResult<Void> success() {
        return new StandardResult<>(true, "", null);
    }

    public static StandardResult<Void> fail(String msg) {
        return new StandardResult<>(false, msg, null);
    }
}
