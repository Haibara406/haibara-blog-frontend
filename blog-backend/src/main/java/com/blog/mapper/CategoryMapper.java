package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 文章分类表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
