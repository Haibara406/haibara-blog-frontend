package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author haibara
 * @description t_like表实体类
 * @since 2025/7/27 14:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_like")
@Schema(description = "点赞实体")
public class Like {

    @Schema(description = "点赞ID", example = "1")
    @TableId(type = IdType.AUTO)
    private String id;

    @Schema(description = "点赞用户ID", example = "1001")
    private Long userId;

    @Schema(description = "点赞类型：1-文章，2-评论", example = "1", allowableValues = {"1", "2"})
    private Integer type;

    @Schema(description = "类型ID（文章ID或评论ID）", example = "1")
    private Integer typeId;

    @Schema(description = "点赞时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "修改时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

