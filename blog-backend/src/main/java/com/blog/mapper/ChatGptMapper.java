package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.ChatGpt;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description ChatGPT对话表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface ChatGptMapper extends BaseMapper<ChatGpt> {
}
