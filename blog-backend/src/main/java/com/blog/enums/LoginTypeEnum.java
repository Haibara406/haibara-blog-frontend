package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 登录类型枚举
 * @since 2025/7/28 15:34
 */
@Getter
@AllArgsConstructor
@Schema(description = "登录类型枚举")
public enum LoginTypeEnum {

    @Schema(description = "前台登录")
    FRONTEND(0,"前台"),

    @Schema(description = "后台登录")
    BACKEND(1,"后台"),

    @Schema(description = "非法登录")
    ILLEGAL(2,"非法登录");

    // 类型
    private final Integer value;
    // 描述
    private final String desc;
}
