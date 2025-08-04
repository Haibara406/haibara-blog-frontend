package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 修改评论是否通过请求
 * @since 2025/7/31 16:55
 */
@Data
@Schema(description = "修改评论是否通过请求")
public class CommentIsCheckDTO {

    @Schema(description = "评论id")
    @NotNull(message = ValidationConstants.COMMENT_ID_NOT_NULL)
    private Long id;

    @Schema(description = "是否通过")
    @NotNull(message = ValidationConstants.COMMENT_IS_CHECK_NOT_NULL)
    private Integer isCheck;
}

