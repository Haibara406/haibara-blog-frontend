package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 文章标签关系表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}
