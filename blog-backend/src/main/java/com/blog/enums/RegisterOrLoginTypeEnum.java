package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 注册类型枚举
 * @since 2025/7/27 15:54
 */
@Getter
@AllArgsConstructor
@Schema(description = "注册或登录类型枚举")
public enum RegisterOrLoginTypeEnum {

    /**
     * 邮箱或用户名登录
     */
    @Schema(description = "邮箱登录")
    EMAIL(0, "邮箱登录", "email"),
    /**
     * Gitee
     */
    @Schema(description = "Gitee登录")
    GITEE(1, "Gitee登录", "gitee"),
    /**
     * Github
     */
    @Schema(description = "Github登录")
    GITHUB(2, "Github登录", "github");


    /**
     * 注册方式
     */
    private final Integer registerType;

    /**
     * 描述
     */
    private final String description;

    /**
     * 策略
     */
    private final String strategy;

}

