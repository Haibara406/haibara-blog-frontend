package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 用户表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
