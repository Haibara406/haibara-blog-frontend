package com.blog.utils;

import com.blog.domain.response.ResponseResult;

import java.util.function.Supplier;

/**
 * @author haibara
 * @description 控制器工具类
 * @since 2025/7/27 16:26
 */
public class ControllerUtils {
    public static  <T> ResponseResult<T> messageHandler(Supplier<T> supplier) {
        return ResponseResult.success(supplier.get());
    }
}