package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author haibara
 * @description 菜单类型枚举
 * @since 2025/8/2 13:45
 */
@Getter
@AllArgsConstructor
@Schema(description = "菜单类型枚举")
public enum MenuTypeEnum {


    @Schema(description = "路由菜单 - 用户实际使用的功能菜单")
    ROUTE_MENU(0, "路由菜单"),

    @Schema(description = "管理菜单 - 后台管理员配置菜单的界面")
    MANAGE_MENU(1, "管理菜单");


    private final Integer type;

    private final String desc;

}