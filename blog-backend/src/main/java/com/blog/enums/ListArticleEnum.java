package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 查询文章的方式枚举
 * @since 2025/7/29 19:24
 */

@Getter
@AllArgsConstructor
@Schema(description = "查询文章方式枚举")
public enum ListArticleEnum {

    @Schema(description = "根据分类查询文章")
    BY_CATEGORY(1, "根据分类查询文章"),

    @Schema(description = "根据标签查询文章")
    BY_TAG(2, "根据标签查询文章");

    private final Integer type;

    private final String desc;
}
