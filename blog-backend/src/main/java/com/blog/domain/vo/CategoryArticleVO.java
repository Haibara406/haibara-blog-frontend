package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 分类下的文章返回
 * @since 2025/7/29 17:54
 */
@Data
@Schema(name = "CategoryArticleVO", description = "分类下的文章vo")
public class CategoryArticleVO {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "分类id")
    private Long categoryId;

    @Schema(description = "文章标签")
    private List<TagVO> tags;

    @Schema(description = "文章缩略图")
    private String articleCover;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "访问量")
    private Long visitCount;

    @Schema(description = "文章创建时间")
    private Date createTime;
}
