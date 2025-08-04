package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.Log;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 系统操作日志表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {
}
