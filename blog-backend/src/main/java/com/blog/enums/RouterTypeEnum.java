package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 路由类型枚举
 * @since 2025/8/2 13:46
 */
@Getter
@AllArgsConstructor
@Schema(description = "路由类型枚举")
public enum RouterTypeEnum {


    @Schema(description = "普通路由 - 加载Vue组件")
    NORMAL(0L, "普通路由", "RouteView"),

    @Schema(description = "内嵌路由 - 使用Iframe嵌入外部页面")
    IFRAME(1L, "内嵌路由", "Iframe"),

    @Schema(description = "外链路由 - 新窗口打开外部链接")
    EXTERNAL(2L, "外链路由", null),

    @Schema(description = "目录路由 - 父菜单容器")
    DIRECTORY(3L, "目录路由", "RouteView");


    private final Long type;

    private final String desc;

    private final String component;

}