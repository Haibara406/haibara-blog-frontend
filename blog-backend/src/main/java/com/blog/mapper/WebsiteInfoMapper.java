package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.WebsiteInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author haibara
 * @description 网站信息表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface WebsiteInfoMapper extends BaseMapper<WebsiteInfo> {
}
