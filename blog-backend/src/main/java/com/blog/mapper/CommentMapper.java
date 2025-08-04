package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 评论表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
