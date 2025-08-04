package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 查询用户角色的类型枚举
 * @since 2025/7/29 15:18
 */

@Getter
@AllArgsConstructor
@Schema(description = "查询用户角色类型枚举")
public enum SelUserRoleEnum {

    @Schema(description = "拥有用户")
    HAS_USER(0, "拥有用户"),

    @Schema(description = "没有用户")
    NO_USER(1, "没有用户");

    // 类型
    private final Integer type;
    // 描述
    private final String desc;
}
