package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 热门文章返回
 * @since 2025/7/29 17:45
 */
@Data
@Schema(description = "热门文章返回")
public class HotArticleVO {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "访问量")
    private Long visitCount;
}
