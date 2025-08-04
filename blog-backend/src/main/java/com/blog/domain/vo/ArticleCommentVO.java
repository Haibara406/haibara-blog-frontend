package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 文章评论返回
 * @since 2025/7/31 16:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Schema(description = "文章评论返回")
public class ArticleCommentVO {

    @Schema(description = "评论id")
    private Long id;

    @Schema(description = "评论类型 (1文章 2友链 3说说)")
    private Integer commentType;

    @Schema(description = "类型id")
    private Integer typeId;

    @Schema(description = "父评论id")
    private Long parentId;

    @Schema(description = "回复评论id")
    private Long replyId;

    @Schema(description = "评论的内容")
    private String commentContent;

    @Schema(description = "评论用户的id")
    private Long commentUserId;

    @Schema(description = "回复用户的id")
    private Long replyUserId;

    @Schema(description = "评论时间")
    private Date createTime;

    @Schema(description = "评论昵称")
    private String commentUserNickname;

    @Schema(description = "评论头像")
    private String commentUserAvatar;

    @Schema(description = "回复的昵称")
    private String replyUserNickname;

    @Schema(description = "点赞数")
    private Long likeCount;

    @Schema(description = "子评论数")
    private Long childCommentCount;

    @Schema(description = "父评论数")
    private Long parentCommentCount;

    @Schema(description = "子评论")
    private List<ArticleCommentVO> childComment;
}
