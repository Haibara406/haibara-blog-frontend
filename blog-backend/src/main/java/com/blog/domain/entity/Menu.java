package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.blog.domain.BaseData;

import java.util.Date;


/**
 * @author haibara
 * @description sys_menu表实体类
 * @since 2025/7/27 14:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_menu")
@Schema(description = "系统菜单实体")
public class Menu implements BaseData {

    @Schema(description = "菜单ID", example = "1")
    private Long id;

    @Schema(description = "菜单标题", example = "用户管理")
    private String title;

    @Schema(description = "菜单图标", example = "user")
    private String icon;

    @Schema(description = "菜单路径", example = "/system/user")
    private String path;

    @Schema(description = "绑定组件", example = "RouteView")
    private String component;

    @Schema(description = "父菜单重定向地址", example = "/system/user/list")
    private String redirect;

    @Schema(description = "是否固定页签：0-否，1-是", example = "0", allowableValues = {"0", "1"})
    private Integer affix;

    @Schema(description = "父级菜单ID", example = "0")
    private Long parentId;

    @Schema(description = "路由名称", example = "UserManagement")
    private String name;

    @Schema(description = "是否隐藏菜单：0-否，1-是", example = "0", allowableValues = {"0", "1"})
    private Integer hideInMenu;

    @Schema(description = "iframe模式的跳转URL", example = "https://example.com")
    private String url;

    @Schema(description = "是否在面包屑中隐藏：0-否，1-是", example = "0", allowableValues = {"0", "1"})
    private Integer hideInBreadcrumb;

    @Schema(description = "是否隐藏所有子菜单：0-否，1-是", example = "0", allowableValues = {"0", "1"})
    private Integer hideChildrenInMenu;

    @Schema(description = "是否保活：0-否，1-是", example = "1", allowableValues = {"0", "1"})
    private Integer keepAlive;

    @Schema(description = "跳转模式", example = "_self", allowableValues = {"_blank", "_self", "_parent"})
    private String target;

    @Schema(description = "是否禁用：0-否，1-是", example = "0", allowableValues = {"0", "1"})
    private Integer isDisable;

    @Schema(description = "排序号", example = "1")
    private Integer orderNum;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

