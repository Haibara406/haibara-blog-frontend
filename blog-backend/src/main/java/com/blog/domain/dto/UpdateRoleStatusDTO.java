package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 身份状态更新请求
 * @since 2025/7/28 00:09
 */
@Data
@Schema
public class UpdateRoleStatusDTO {

    @NotNull(message = ValidationConstants.USER_ID_NOT_NULL)
    @Schema(description = "用户ID")
    private Long id;

    @Min(value = 0, message =  ValidationConstants.ROLE_STATUS_VALUE_WRONG)
    @Max(value = 1, message =  ValidationConstants.ROLE_STATUS_VALUE_WRONG)
    @Schema(description = "用户状态")
    private Integer status;
}
