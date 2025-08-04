package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 角色状态枚举
 * @since 2025/7/27 15:56
 */
@Getter
@AllArgsConstructor
@Schema(description = "角色状态枚举")
public enum RoleEnum {

    @Schema(description = "状态：正常")
    ROLE_STATUS_NORMAL(0, "状态：正常"),

    @Schema(description = "状态：停用")
    ROLE_STATUS_DEACTIVATE(1, "状态：停用");

    // 类型
    private final Integer value;
    // 描述
    private final String desc;
}
