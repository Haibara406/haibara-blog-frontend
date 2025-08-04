package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author haibara
 * @description 菜单详情返回
 * @since 2025/8/2 12:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "菜单详情返回")
public class MenuByIdVO {
    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "菜单标题")
    private String title;

    @Schema(description = "角色id")
    private List<Long> roleId;

    @Schema(description = "排序号")
    private Integer orderNum;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "路由类型")
    private Long routerType;

    @Schema(description = "绑定组件")
    private String component;

    @Schema(description = "父菜单重定向地址")
    private String redirect;

    @Schema(description = "菜单路径")
    private String path;

    @Schema(description = "iframe模式的跳转URL")
    private String url;

    @Schema(description = "跳转模式", allowableValues = {"_blank", "_self", "_parent"})
    private String target;

    @Schema(description = "是否固定页签：0-否，1-是", allowableValues = {"0", "1"})
    private Integer affix;

    @Schema(description = "是否保活：0-否，1-是", allowableValues = {"0", "1"})
    private Integer keepAlive;

    @Schema(description = "是否隐藏菜单：0-否，1-是", allowableValues = {"0", "1"})
    private Integer hideInMenu;

    @Schema(description = "是否禁用：0-否，1-是", allowableValues = {"0", "1"})
    private Integer isDisable;
}
