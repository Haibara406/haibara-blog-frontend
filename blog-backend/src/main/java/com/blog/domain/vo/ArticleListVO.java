package com.blog.domain.vo;

import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 所有的文章列表返回
 * @since 2025/7/29 17:58
 */
@Data
public class ArticleListVO implements BaseData {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "分类id")
    private Long categoryId;

    @Schema(description = "作者id")
    private Long userId;

    @Schema(description = "作者名称")
    private String userName;

    @Schema(description = "类型 (1原创 2转载 3翻译)", allowableValues = {"1", "2", "3"})
    private Integer articleType;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "文章标签名称")
    private List<String> tagsName;

    @Schema(description = "文章缩略图")
    private String articleCover;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "是否置顶 (0否 1是）", allowableValues = {"0", "1"})
    private Integer isTop;

    @Schema(description = "文章状态 (1公开 2私密 3草稿)", allowableValues = {"1", "2", "3"})
    private Integer status;

    @Schema(description = "访问量")
    private Long visitCount;

    @Schema(description = "文章创建时间")
    private Date createTime;
}
