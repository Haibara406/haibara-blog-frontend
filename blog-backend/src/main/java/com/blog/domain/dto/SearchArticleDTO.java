package com.blog.domain.dto;

import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 搜索文章列表请求
 * @since 2025/7/29 18:07
 */
@Data
@Schema(description = "搜索文章列表请求")
public class SearchArticleDTO implements BaseData {

    @Schema(description = "分类id")
    private Long categoryId;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "文章状态(1公开 2私密 3草稿)", allowableValues = {"1", "2", "3"})
    private Integer status;

    @Schema(description = "是否置顶(0否 1是)")
    private Integer isTop;
}
