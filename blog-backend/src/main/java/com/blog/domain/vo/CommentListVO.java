package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 评论列表返回
 * @since 2025/7/31 16:52
 */
@Data
@Schema(description = "评论列表返回")
public class CommentListVO {

    @Schema(description = "评论id")
    private Long id;

    @Schema(description = "评论类型 (1文章 2留言板)")
    private Integer type;

    @Schema(description = "类型id")
    private Integer typeId;

    @Schema(description = "父评论id")
    private Long parentId;

    @Schema(description = "回复评论id")
    private Long replyId;

    @Schema(description = "评论的内容")
    private String commentContent;

    @Schema(description = "评论用户的名称")
    private String commentUserName;

    @Schema(description = "是否通过 (0否 1是)")
    private Integer isCheck;

    @Schema(description = "评论时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
