package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.domain.entity.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author haibara
 * @description 系统操作日志表数据库访问层
 * @since 2025/7/27
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {
    
    /**
     * 查询最旧的指定数量的非保护操作记录ID
     * 
     * @param protectedOperations 受保护的操作类型列表
     * @param limit 查询数量
     * @return 记录ID列表
     */
    @Select("<script>" +
            "SELECT id FROM sys_log WHERE is_deleted = 0 " +
            "AND operation NOT IN " +
            "<foreach collection='protectedOperations' item='operation' open='(' separator=',' close=')'>" +
            "#{operation}" +
            "</foreach> " +
            "ORDER BY create_time ASC LIMIT #{limit}" +
            "</script>")
    List<Long> selectOldestNonProtectedIds(@Param("protectedOperations") List<String> protectedOperations, 
                                         @Param("limit") long limit);
    
    /**
     * 查询最旧的指定数量的受保护操作记录ID
     * 
     * @param protectedOperations 受保护的操作类型列表
     * @param limit 查询数量
     * @return 记录ID列表
     */
    @Select("<script>" +
            "SELECT id FROM sys_log WHERE is_deleted = 0 " +
            "AND operation IN " +
            "<foreach collection='protectedOperations' item='operation' open='(' separator=',' close=')'>" +
            "#{operation}" +
            "</foreach> " +
            "ORDER BY create_time ASC LIMIT #{limit}" +
            "</script>")
    List<Long> selectOldestProtectedIds(@Param("protectedOperations") List<String> protectedOperations,
                                      @Param("limit") long limit);
    
    /**
     * 根据操作类型统计数量
     * 
     * @param operation 操作类型
     * @return 记录数量
     */
    @Select("SELECT COUNT(*) FROM sys_log WHERE operation = #{operation} AND is_deleted = 0")
    Long countByOperation(@Param("operation") String operation);
}
