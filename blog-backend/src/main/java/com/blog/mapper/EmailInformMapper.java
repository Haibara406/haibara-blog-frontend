package com.blog.mapper;

import com.blog.domain.email.CommentEmail;

/**
 * @author haibara
 * @description 邮箱信息
 * @since 2025/7/27 15:02
 */
public interface EmailInformMapper {

    /**
     * 查询用户评论信息
     * @param commentId 评论id
     * @param type 评论类型
     * @return 需要的信息
     */
    CommentEmail getCommentEmailOne(String commentId, Integer type);
}
