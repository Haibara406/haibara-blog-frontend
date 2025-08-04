package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 删除日志请求
 * @since 2025/7/28 19:33
 */
@Data
@Schema(description = "删除日志请求")
public class LogDeleteDTO {

    @NotNull(message = ValidationConstants.LOG_ID_NOT_NULL)
    @Schema(description = "日志id列表")
    List<Long> Ids;
}
