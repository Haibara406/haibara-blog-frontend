package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 角色菜单关系表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
}
