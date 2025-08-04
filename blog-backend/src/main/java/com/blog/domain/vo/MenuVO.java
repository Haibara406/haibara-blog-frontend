package com.blog.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description 管理菜单返回
 * @since 2025/8/2 12:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "管理菜单返回")
public class MenuVO {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "菜单标题")
    private String title;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "菜单路径")
    private String path;

    @Schema(description = "绑定组件")
    private String component;

    @Schema(description = "父菜单重定向地址")
    private String redirect;

    @Schema(description = "是否固定页签：0-否，1-是", allowableValues = {"0", "1"})
    private Boolean affix;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "路由名称")
    private String name;

    @Schema(description = "是否隐藏菜单：0-否，1-是", allowableValues = {"0", "1"})
    private Boolean hideInMenu;

    @Schema(description = "iframe模式的跳转URL")
    private String url;

    @Schema(description = "是否保活：0-否，1-是", allowableValues = {"0", "1"})
    private Boolean keepAlive;

    @Schema(description = "跳转模式", allowableValues = {"_blank", "_self", "_parent"})
    private String target;

    @Schema(description = "是否禁用：0-否，1-是", allowableValues = {"0", "1"})
    private Boolean isDisable;

    @Schema(description = "排序号")
    private Integer orderNum;

    @Schema(description = "创建时间")
    private Date createTime;
}
