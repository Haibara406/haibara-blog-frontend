package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.blog.domain.BaseData;

import java.util.Date;


/**
 * @author haibara
 * @description t_photo表实体类
 * @since 2025/7/27 14:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value =  "t_photo")
@Schema(description = "照片实体")
public class Photo implements BaseData {

    @Schema(description = "照片ID", example = "1")
    private Long id;

    @Schema(description = "创建者ID", example = "1001")
    private Long userId;

    @Schema(description = "照片名称", example = "美丽的风景")
    private String name;

    @Schema(description = "照片描述", example = "这是一张美丽的风景照片")
    private String description;

    @Schema(description = "类型：1-相册，2-照片", example = "2", allowableValues = {"1", "2"})
    private Integer type;

    @Schema(description = "父相册ID", example = "1")
    private Long parentId;

    @Schema(description = "图片地址", example = "https://example.com/photo.jpg")
    private String url;

    @Schema(description = "是否通过审核：0-否，1-是", example = "1", allowableValues = {"0", "1"})
    private Integer isCheck;

    @Schema(description = "照片体积大小(KB)", example = "1024.5")
    private Double size;
    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

