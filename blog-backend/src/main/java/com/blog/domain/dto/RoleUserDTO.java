package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 角色用户关系操作请求
 * @since 2025/7/29 14:59
 */
@Data
@Schema(description = "角色用户关系操作请求")
public class RoleUserDTO {

    @NotNull(message = ValidationConstants.USER_ID_NOT_NULL)
    @Schema(description = "用户id")
    private List<Long> userId;

    @NotNull(message = ValidationConstants.ROLE_ID_NOT_NULL)
    @Schema(description = "角色id列表")
    private List<Long> roleId;
}
