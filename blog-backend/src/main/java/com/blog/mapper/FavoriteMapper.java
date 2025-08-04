package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 收藏表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
