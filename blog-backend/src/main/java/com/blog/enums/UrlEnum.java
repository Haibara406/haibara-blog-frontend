package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description url枚举
 * @since 2025/7/27 15:57
 */
@Getter
@AllArgsConstructor
@Schema(description = "URL配置枚举")
public enum UrlEnum {

    /**
     * Gitee解析Token获取个人信息
     */
    @Schema(description = "Gitee解析Token获取个人信息")
    GITEE_USER_INFO("https://gitee.com/api/v5/user", "GET", "Gitee解析Token获取个人信息"),

    /**
     * Github解析Token获取个人信息
     */
    @Schema(description = "Github解析Token获取个人信息")
    GITHUB_USER_INFO("https://api.github.com/user", "GET", "Github解析Token获取个人信息");

    /**
     * url
     */
    private final String url;

    /**
     * 请求方法
     */
    private final String method;

    /**
     * 描述
     */
    private final String description;
}
