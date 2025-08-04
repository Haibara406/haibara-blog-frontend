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
 * @description t_favorite表实体类
 * @since 2025/7/27 14:02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_favorite")
@Schema(description = "收藏实体")
public class Favorite implements BaseData {

    @Schema(description = "收藏ID", example = "1")
    private Long id;

    @Schema(description = "收藏用户ID", example = "1001")
    private Long userId;

    @Schema(description = "收藏类型：1-文章，2-留言板", example = "1", allowableValues = {"1", "2"})
    private Integer type;

    @Schema(description = "类型ID（文章ID或留言板ID）", example = "1")
    private Long typeId;

    @Schema(description = "是否有效：0-否，1-是", example = "1", allowableValues = {"0", "1"})
    private Integer isCheck;

    @Schema(description = "收藏时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}

