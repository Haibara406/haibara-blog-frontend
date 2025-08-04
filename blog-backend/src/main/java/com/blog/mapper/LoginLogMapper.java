package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 登录日志表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
