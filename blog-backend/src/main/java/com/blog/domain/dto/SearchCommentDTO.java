package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 搜索评论请求
 * @since 2025/7/31 16:54
 */
@Data
@Schema(description = "搜索评论请求")
public class SearchCommentDTO {


    @Schema(description = "评论用户的名称")
    private String commentUserName;

    @Schema(description = "评论的内容")
    private String commentContent;

    @Schema(description = "评论类型 (1文章 2留言板)")
    private Integer type;

    @Schema(description = "是否通过 (0否 1是)")
    private Integer isCheck;
}