package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author haibara
 * @description 权限操作相关请求
 * @since 2025/7/29 13:39
 */
@Accessors(chain = true)
@Data
@Schema(description = "权限操作相关请求")
public class PermissionDTO implements BaseData {

    @Schema(description = "权限ID")
    private Integer id;

    @NotNull(message = ValidationConstants.PERM_DESC_NOT_NULL)
    @Schema(description = "权限描述")
    private String permissionDesc;

    @NotNull(message = ValidationConstants.PERM_KEY_NOT_NULL)
    @Schema(description = "权限标识")
    private String permissionKey;


    @NotNull(message = ValidationConstants.PERM_MENU_ID_NOT_NULL)
    @Schema(description = "所在菜单ID")
    private Long permissionMenuId;
}
