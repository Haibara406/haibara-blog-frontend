package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 查询角色用户类型枚举
 * @since 2025/7/29 15:05
 */

@Getter
@AllArgsConstructor
@Schema(description = "查询角色用户类型枚举")
public enum SelRoleUserEnum {

    @Schema(description = "拥有角色")
    HAS_ROLE(0, "拥有角色"),

    @Schema(description = "没有角色")
    NO_ROLE(1, "没有角色");

    // 类型
    private final Integer type;
    // 描述
    private final String desc;
}
