package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 点赞类型枚举
 * @since 2025/7/27 15:53
 */
@Getter
@AllArgsConstructor
@Schema(description = "点赞类型枚举")
public enum LikeEnum {

    @Schema(description = "点赞文章")
    LIKE_TYPE_ARTICLE(1, "点赞：文章"),

    @Schema(description = "点赞评论")
    LIKE_TYPE_COMMENT(2, "点赞：评论"),

    @Schema(description = "点赞留言")
    LIKE_TYPE_LEAVE_WORD(3, "点赞：留言");

    // 类型
    private final Integer type;
    // 描述
    private final String desc;
}