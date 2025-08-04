package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 系统角色表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
