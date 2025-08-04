package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 修改友链是否通过请求
 * @since 2025/8/1 13:20
 */
@Data
@Schema(description = "修改友链是否通过请求")
public class LinkIsCheckDTO {

    @NotNull(message = ValidationConstants.LINK_ID_NOT_NULL)
    @Schema(description = "友链id")
    private Long id;

    @NotNull(message = ValidationConstants.LINK_IS_CHECK_NOT_NULL)
    @Schema(description = "是否通过")
    private Integer isCheck;
}
