package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 删除登录日志请求
 * @since 2025/7/28 15:21
 */

@Data
@Schema(description = "删除登录日志请求")
public class LoginLogDeleteDTO {

    @Schema(description = "登录日志id列表")
    @NotNull(message = ValidationConstants.LOGIN_LOG_ID_NOT_NULL)
    List<Long> Ids;
}
