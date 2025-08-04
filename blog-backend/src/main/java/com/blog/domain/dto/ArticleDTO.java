package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 发布文章请求
 * @since 2025/7/29 17:55
 */
@Data
@Schema(description = "发布文章请求")
public class ArticleDTO implements BaseData {

    @Schema(description = "文章id")
    private Long id;

    @NotNull(message = ValidationConstants.CATEGORY_ID_NOT_NULL)
    @Schema(description = "分类id")
    private Long categoryId;

    @NotNull(message = ValidationConstants.TAG_ID_NOT_NULL)
    @Schema(description = "标签id")
    private List<Long> tagId;

    @NotNull(message = ValidationConstants.ARTICLE_COVER_NOT_NULL)
    @Schema(description = "文章缩略图")
    private String articleCover;

    @NotNull(message = ValidationConstants.ARTICLE_TITLE_NOT_NULL)
    @Schema(description = "文章标题")
    private String articleTitle;

    @NotNull(message = ValidationConstants.ARTICLE_CONTENT_NOT_NULL)
    @Schema(description = "文章内容")
    private String articleContent;

    @NotNull(message = ValidationConstants.ARTICLE_TYPE_NOT_NULL)
    @Schema(description = "文章类型")
    private Integer articleType;

    @NotNull(message = ValidationConstants.IS_TOP_NOT_NULL)
    @Schema(description = "是否置顶")
    private Integer isTop;

    @NotNull(message = ValidationConstants.ARTICLE_STATUS_NOT_NULL)
    @Schema(description = "文章状态")
    private Integer status;
}
