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
 * @description t_banners表实体类
 * @since 2025/7/27 13:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_banners")
@Schema(description = "轮播图实体")
public class Banners implements BaseData {

    @Schema(description = "轮播图ID", example = "1")
    private Long id;

    @Schema(description = "图片路径", example = "https://example.com/banner.jpg")
    private String path;

    @Schema(description = "图片大小（字节）", example = "1024000")
    private Long size;

    @Schema(description = "图片类型（MIME）", example = "image/jpeg")
    private String type;

    @Schema(description = "上传人ID", example = "1001")
    private Long userId;

    @Schema(description = "图片排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}

