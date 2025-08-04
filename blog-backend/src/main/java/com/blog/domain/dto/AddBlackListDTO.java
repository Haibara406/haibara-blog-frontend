package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 添加黑名单请求
 * @since 2025/7/27 16:42
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBlackListDTO {


    @Schema(description = "用户Id")
    @Size(min = 1, message = ValidationConstants.USER_ID_NOT_NULL)
    private List<Long> userIds;


    @Schema(description = "封禁理由")
    @NotBlank(message = ValidationConstants.BAN_REASON_NOT_NULL)
    private String reason;


    @Schema(description = "封禁到期时间")
    @NotNull(message = ValidationConstants.BAN_EXPIRE_TIME_NOT_NULL)
    @Future(message = ValidationConstants.BAN_EXPIRE_TIME_MUST_BE_FUTURE)
    private Date expiresTime;
}
