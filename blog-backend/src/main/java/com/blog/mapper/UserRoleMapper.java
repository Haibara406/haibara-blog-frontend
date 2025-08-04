package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 用户角色关系表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
