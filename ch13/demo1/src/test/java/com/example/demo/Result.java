package com.example.demo;

import lombok.Getter;
import lombok.ToString;

/**
 * @ClassName Result
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/2 下午6:36
 * @Version 1.0
 */
@Getter
@ToString
public class Result<T> {
    private final boolean success;
    private final String message;
    private final T data;

    private Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <D> Result<D> success(D data, String message){
        return new Result<D>(true, message, data);
    }

    public static <D> Result<D> success(D data){
        return new Result<D>(true, "", data);
    }

    public static Result<Void> fail(String message){
        return new Result<>(false, message, null);
    }
}
