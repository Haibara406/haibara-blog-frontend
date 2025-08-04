package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haibara
 * @description sys_role_permission表实体类
 * @since 2025/7/27 14:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("sys_role_permission")
@Schema(description = "角色权限关系实体")
public class RolePermission {

    @Schema(description = "关系表ID", example = "1")
    private Integer id;

    @Schema(description = "角色ID", example = "1")
    @TableId
    private Long roleId;

    @Schema(description = "权限ID", example = "1")
    private Long permissionId;
}

