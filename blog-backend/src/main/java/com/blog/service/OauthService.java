package com.blog.service;

import jakarta.servlet.http.HttpServletRequest;
import me.zhyd.oauth.model.AuthResponse;

/**
 * @author haibara
 * @description 第三方登录服务类
 * @since 2025/7/27 15:31
 */
public interface OauthService {

    /**
     * 处理第三方登录
     *
     * @param authResponse 授权响应
     * @param request      请求
     * @param type         登录类型
     * @return 响应结果
     */
    String handleLogin(AuthResponse authResponse, HttpServletRequest request, Integer type);
}
