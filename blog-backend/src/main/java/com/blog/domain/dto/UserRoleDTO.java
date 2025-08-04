package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 用户角色操作请求
 * @since 2025/7/29 14:57
 */
@Data
@Schema(description = "用户角色操作请求")
public class UserRoleDTO {

    @NotNull(message = ValidationConstants.ROLE_ID_NOT_NULL)
    @Schema(description = "角色id")
    private Long roleId;

    @NotNull(message = ValidationConstants.USER_ID_NOT_NULL)
    @Schema(description = "用户id列表")
    private List<Long> userId;
}