package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.BlackList;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author haibara
 * @description 黑名单表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface BlackListMapper extends BaseMapper<BlackList> {

    @Delete("DELETE FROM t_black_list WHERE ip_info -> '$.createIp' = #{ip}")
    Long deleteByIp(String ip);

    // 查询是否存在ip
    @Select("SELECT id FROM t_black_list WHERE ip_info -> '$.createIp' = #{ip}")
    Long getIdByIp(String ip);
}
