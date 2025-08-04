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
 * @description t_link表实体类
 * @since 2025/7/27 14:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_link")
@Schema(description = "友情链接实体")
public class Link implements BaseData {

    @Schema(description = "友链ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "网站名称", example = "技术博客")
    private String name;

    @Schema(description = "网站地址", example = "https://example.com")
    private String url;

    @Schema(description = "网站描述", example = "一个优秀的技术博客")
    private String description;

    @Schema(description = "网站背景图", example = "https://example.com/bg.jpg")
    private String background;

    @Schema(description = "邮箱地址", example = "admin@example.com")
    private String email;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "审核状态：0-未通过，1-已通过", example = "1", allowableValues = {"0", "1"})
    private Integer isCheck;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

