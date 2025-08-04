package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 角色权限操作相关请求
 * @since 2025/7/29 11:49
 */
@Data
@Schema(description = "角色权限操作相关请求")
public class RolePermissionDTO {

    @NotNull(message = ValidationConstants.PERMISSION_ID_NOT_NULL)
    @Schema(description = "权限id列表")
    private List<Long> permissionId;

    @NotNull(message = ValidationConstants.ROLE_ID_NOT_NULL)
    @Schema(description = "角色id列表")
    private List<Long> roleId;
}
