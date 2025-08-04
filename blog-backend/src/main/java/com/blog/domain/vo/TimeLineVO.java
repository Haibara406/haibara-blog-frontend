package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 时间轴返回
 * @since 2025/7/29 17:54
 */
@Data
public class TimeLineVO {

    @Schema(description = "文章id")
    private Long id;

    @Schema(description = "文章缩略图")
    private String articleCover;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "文章内容")
    private String articleContent;

    @Schema(description = "文章创建时间")
    private Date createTime;
}
