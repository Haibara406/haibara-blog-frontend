package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 分类返回
 * @since 2025/7/30 19:23
 */
@Data
@Schema(name = "CategoryVO", description = "分类VO")
public class CategoryVO {

    @Schema(description = "分类id")
    private Long id;

    @Schema(description = "分类名")
    private String categoryName;

    @Schema(description = "分类下的文章数量")
    private Long articleCount;

    @Schema(description = "分类创建时间")
    private Date createTime;

    @Schema(description = "分类更新时间")
    private Date updateTime;
}