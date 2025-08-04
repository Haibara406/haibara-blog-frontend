package com.blog.annotation;

import java.lang.annotation.*;

/**
 * @author haibara
 * @description 系统日志记录
 * @since 2025/7/27 16:14
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String module() default "";
    String operation() default "";
}