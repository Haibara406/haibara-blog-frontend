package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 根据内容搜索文章返回
 * @since 2025/7/29 17:43
 */
@Data
@Schema(description = "根据内容搜索文章返回")
public class SearchArticleByContentVO {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "访问量")
    private Long visitCount;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "内容")
    private String articleContent;
}