package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 删除用户请求
 * @since 2025/7/28 00:21
 */
@Data
@Schema
public class UserDeleteDTO {

    @NotNull(message = ValidationConstants.USER_ID_NOT_NULL)
    @Schema(description = "用户id列表")
    List<Long> Ids;
}
