package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 查询权限枚举
 * @since 2025/7/29 12:11
 */

@Getter
@AllArgsConstructor
@Schema(description = "查询权限类型枚举")
public enum SelPermTypeEnum {

    @Schema(description = "拥有权限")
    HAS_PERMISSION(0, "拥有权限"),

    @Schema(description = "没有权限")
    NO_PERMISSION(1, "没有权限");

    // 类型
    private final Integer type;
    // 描述
    private final String desc;
}
