package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 收藏类型枚举
 * @since 2025/7/27 15:53
 */
@Getter
@AllArgsConstructor
@Schema(description = "收藏类型枚举")
public enum FavoriteEnum {

    @Schema(description = "收藏文章")
    FAVORITE_TYPE_ARTICLE(1, "收藏：文章"),

    @Schema(description = "收藏留言")
    FAVORITE_TYPE_LEAVE_WORD(2, "收藏：留言");

    // 类型
    private final Integer type;
    // 描述
    private final String desc;
}
