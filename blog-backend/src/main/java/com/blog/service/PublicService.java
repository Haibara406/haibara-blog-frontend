package com.blog.service;

import java.util.Map;

/**
 * @author haibara
 * @description 发布服务接口
 * @since 2025/7/27 15:35
 */
public interface PublicService {

    /**
     * 邮箱验证码发送
     * @param type 邮箱类型
     * @param email 邮箱地址
     * @return 是否成功
     */
    String registerEmailVerifyCode(String type, String email);

    /**
     * 邮箱通知
     * @param type 邮箱类型
     * @param email 邮箱地址
     * @param content 邮箱内容
     */
    void sendEmail(String type, String email, Map<String, Object> content);
}
