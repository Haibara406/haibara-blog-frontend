package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 权限返回
 * @since 2025/7/29 13:36
 */
@Data
@Schema(description = "权限返回")
public class PermissionVO {

    @Schema(description = "权限ID")
    private Integer id;

    @Schema(description = "权限描述")
    private String permissionDesc;

    @Schema(description = "权限标识")
    private String permissionKey;

    @Schema(description = "菜单名称")
    private String menuName;
}
