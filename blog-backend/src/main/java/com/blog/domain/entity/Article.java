package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description t_article表实体类
 * @since 2025/7/27 13:36
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@TableName("t_article")
@Schema(description = "文章实体")
public class Article implements BaseData {
    @Schema(description = "文章ID", example = "1")
    private Long id;

    @Schema(description = "作者ID", example = "1001")
    private Long userId;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "文章缩略图URL", example = "https://example.com/cover.jpg")
    private String articleCover;

    @Schema(description = "文章标题", example = "Spring Boot 入门教程")
    private String articleTitle;

    @Schema(description = "文章内容", example = "这是一篇关于Spring Boot的详细教程...")
    private String articleContent;

    @Schema(description = "文章类型：1-原创，2-转载，3-翻译", example = "1", allowableValues = {"1", "2", "3"})
    private Integer articleType;

    @Schema(description = "是否置顶：0-否，1-是", example = "0", allowableValues = {"0", "1"})
    private Integer isTop;

    @Schema(description = "文章状态：1-公开，2-私密，3-草稿", example = "1", allowableValues = {"1", "2", "3"})
    private Integer status;

    @Schema(description = "访问量", example = "1000")
    private Long visitCount;
    @Schema(description = "文章创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "文章更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}