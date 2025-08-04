package com.blog.annotation;

import java.lang.annotation.*;

/**
 * @author haibara
 * @description 封禁验证注解
 * @since 2025/7/27 16:14
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckBlacklist {
}
