package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 文章详情返回
 * @since 2025/7/29 17:51
 */
@Data
@Schema(name = "ArticleDetailVO", description = "文章详情VO")
public class ArticleDetailVO {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "作者id")
    private Long userId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "分类id")
    private Long categoryId;

    @Schema(description = "文章标签")
    private List<TagVO> tags;

    @Schema(description = "文章缩略图")
    private String articleCover;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "文章内容")
    private String articleContent;

    @Schema(description = "类型 (1原创 2转载 3翻译)")
    private Integer articleType;

    @Schema(description = "是否置顶 (0否 1是）")
    private Integer isTop;

    @Schema(description = "访问量")
    private Long visitCount;

    @Schema(description = "评论数")
    private Long commentCount;

    @Schema(description = "点赞数")
    private Long likeCount;

    @Schema(description = "收藏数")
    private Long favoriteCount;

    @Schema(description = "上一篇文章id")
    private Long preArticleId;

    @Schema(description = "上一篇文章标题")
    private String preArticleTitle;

    @Schema(description = "下一篇文章标题")
    private String nextArticleTitle;

    @Schema(description = "下一篇文章id")
    private Long nextArticleId;

    @Schema(description = "文章创建时间")
    private Date createTime;

    @Schema(description = "文章更新时间")
    private Date updateTime;
}
