package com.blog.config;

import com.blog.interceptor.AccessLimitInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author haibara
 * @description 拦截器配置类
 * @since 2025/7/27 16:35
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // redis限流拦截器
        registry.addInterceptor(accessLimitInterceptor).addPathPatterns("/**");
    }
}
