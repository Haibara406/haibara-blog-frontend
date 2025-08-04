package com.blog.domain.email;

import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author haibara
 * @description 评论
 * @since 2025/7/27 13:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论邮件通知实体")
public class CommentEmail implements Serializable, BaseData {

    @Schema(description = "评论ID", example = "1")
    private Long id;

    @Schema(description = "评论类型：1-文章评论，2-友链评论", example = "1", allowableValues = {"1", "2"})
    private Integer type;

    @Schema(description = "类型ID（文章ID或友链ID）", example = "1")
    private Long typeId;

    @Schema(description = "评论标题", example = "Spring Boot 入门教程")
    private String title;

    @Schema(description = "文章地址", example = "https://example.com/article/1")
    private String url;

    @Schema(description = "评论人头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "评论人昵称", example = "张三")
    private String nickname;

    @Schema(description = "评论内容", example = "这是一条评论")
    private String content;

    @Schema(description = "评论时间", example = "2025-07-27 14:30:00")
    private String time;
}