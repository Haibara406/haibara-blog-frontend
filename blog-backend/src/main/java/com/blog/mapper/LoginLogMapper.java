package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author haibara
 * @description 登录日志表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
    
    /**
     * 查询最旧的指定数量的记录ID
     * 
     * @param limit 查询数量
     * @return 记录ID列表
     */
    @Select("SELECT id FROM sys_login_log WHERE is_deleted = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<Long> selectOldestIds(long limit);
}
