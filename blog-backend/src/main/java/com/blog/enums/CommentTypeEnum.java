package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 评论类型枚举
 * @since 2025/7/31 17:33
 */
@Schema(description = "评论类型枚举")
@Getter
@AllArgsConstructor
public enum CommentTypeEnum {

    @Schema(description = "文章评论")
    ARTICLE_COMMENT(1, "文章评论"),

    @Schema(description = "留言板评论")
    LEAVE_WORD_COMMENT(2, "留言板评论");

    // 类型
    private final Integer type;
    // 描述
    private final String desc;
}
