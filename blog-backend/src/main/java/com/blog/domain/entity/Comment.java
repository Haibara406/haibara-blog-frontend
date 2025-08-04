package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description t_comment表实体类
 * @since 2025/7/27 14:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_comment")
@Schema(description = "评论实体")
public class Comment implements BaseData {
    @Schema(description = "评论ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "评论类型：1-文章评论，2-留言板评论", example = "1", allowableValues = {"1", "2"})
    private Integer type;

    @Schema(description = "类型ID（文章ID或留言板ID）", example = "1")
    private Integer typeId;

    @Schema(description = "父评论ID", example = "0")
    private Long parentId;

    @Schema(description = "回复评论ID", example = "0")
    private Long replyId;

    @Schema(description = "评论内容", example = "这是一条评论")
    private String commentContent;

    @Schema(description = "评论用户ID", example = "1001")
    private Long commentUserId;

    @Schema(description = "回复用户ID", example = "1002")
    private Long replyUserId;
    @Schema(description = "是否通过审核：0-否，1-是", example = "1", allowableValues = {"0", "1"})
    private Integer isCheck;

    @Schema(description = "评论时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}
