package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 用户添加评论请求
 * @since 2025/7/31 16:41
 */
@Data
@Schema(description = "用户添加评论请求")
public class UserCommentDTO implements BaseData {

    @Schema(description = "评论类型 (1文章 2留言板)")
    @NotNull(message = ValidationConstants.COMMENT_TYPE_NOT_NULL)
    private Integer type;

    @Schema(description = "类型id")
    @NotNull(message = ValidationConstants.COMMENT_TYPE_ID_NOT_NULL)
    private Integer typeId;

    @Schema(description = "父评论id")
    private Long parentId;

    @Schema(description = "回复评论id")
    private Long replyId;

    @Schema(description = "评论的内容")
    @NotNull(message = ValidationConstants.COMMENT_CONTENT_NOT_NULL)
    private String commentContent;

    @Schema(description = "回复用户的id")
    private Long replyUserId;
}