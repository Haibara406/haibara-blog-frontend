package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description t_category表实体类
 * @since 2025/7/27 14:00
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_category")
@Schema(description = "文章分类实体")
public class Category implements BaseData {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "技术分享")
    private String categoryName;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}
