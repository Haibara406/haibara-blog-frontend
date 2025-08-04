package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.blog.domain.BaseData;


/**
 * @author haibara
 * @description sys_role_menu表实体类
 * @since 2025/7/27 14:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role_menu")
@Schema(description = "角色菜单关系实体")
public class RoleMenu implements BaseData {

    @Schema(description = "主键ID", example = "1")
    private Integer id;

    @Schema(description = "角色ID", example = "1")
    @TableId
    private Long roleId;

    @Schema(description = "菜单ID", example = "1")
    private Long menuId;

    public RoleMenu(Long roleId, Long menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }
}

