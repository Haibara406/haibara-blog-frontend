package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 相关文章返回
 * @since 2025/7/29 17:53
 */
@Data
@Schema(name = "RelatedArticleVO", description = "相关文章VO")
public class RelatedArticleVO {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "文章缩略图")
    private String articleCover;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "文章创建时间")
    private Date createTime;
}
