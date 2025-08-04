package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 评论类型枚举
 * @since 2025/7/27 15:52
 */

@Getter
@AllArgsConstructor
@Schema(description = "评论类型枚举")
public enum CommentEnum {

    @Schema(description = "文章评论")
    COMMENT_TYPE_ARTICLE(1, "评论类型(1,文章)"),

    @Schema(description = "留言板评论")
    COMMENT_TYPE_LEAVE_WORD(2, "评论类型(2,留言板)");

    // 评论类型
    private final Integer type;
    // 描述
    private final String desc;
}

