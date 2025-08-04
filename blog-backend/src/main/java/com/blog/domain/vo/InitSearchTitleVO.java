package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 初始化标题搜索请求
 * @since 2025/7/29 17:42
 */
@Data
@Schema(description = "初始化标题搜索请求")
public class InitSearchTitleVO {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "访问量")
    private Long visitCount;

    @Schema(description = "文章标题")
    private String articleTitle;
}
