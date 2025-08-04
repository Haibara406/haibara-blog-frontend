package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author haibara
 * @description 统计类型枚举
 * @since 2025/7/27 15:52
 */
@Schema(description = "统计类型枚举")
public enum CountTypeEnum {
    @Schema(description = "收藏统计")
    FAVORITE,

    @Schema(description = "点赞统计")
    LIKE,

    @Schema(description = "评论统计")
    COMMENT
}
