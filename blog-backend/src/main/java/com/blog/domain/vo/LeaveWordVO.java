package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author haibara
 * @description 留言返回
 * @since 2025/7/31 14:34
 */
@Data
@Accessors(chain = true)
@Schema(description = "留言返回")
public class LeaveWordVO {

    @Schema(description = "留言id")
    private Long id;

    @Schema(description = "留言用户id")
    private Long userId;

    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "留言时间")
    private Date createTime;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "留言评论数量")
    private Long commentCount;

    @Schema(description = "留言点赞数量")
    private Long likeCount;

    @Schema(description = "留言收藏数量")
    private Long favoriteCount;
}

