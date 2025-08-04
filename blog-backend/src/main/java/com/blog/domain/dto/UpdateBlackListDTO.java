package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description 修改黑名单请求
 * @since 2025/7/28 16:57
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "修改黑名单请求")
public class UpdateBlackListDTO {

    @Schema(description = "id")
    @NotNull(message = ValidationConstants.USER_ID_NOT_NULL)
    private Long id;

    @Schema(description = "封禁理由")
    @NotBlank(message = ValidationConstants.BAN_REASON_NOT_NULL)
    private String reason;

    @Schema(description = "封禁到期时间")
    @NotNull(message = ValidationConstants.BAN_EXPIRE_TIME_NOT_NULL)
//    @Future(message = "封禁到期时间必须大于当前时间")
    private Date expiresTime;
}
