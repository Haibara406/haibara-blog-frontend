package com.blog.service;

/**
 * 日志清理服务接口
 * 
 * @author haibara
 * @description 提供系统日志的自动清理功能
 * @since 2025/8/13
 */
public interface LogCleanupService {
    
    /**
     * 清理登录日志
     * 保留最新的指定数量的记录，删除超出部分
     * 
     * @param keepCount 保留的记录数量
     * @return 删除的记录数量
     */
    int cleanupLoginLogs(int keepCount);
    
    /**
     * 清理操作日志
     * 保留最新的指定数量的记录，删除超出部分
     * 保护重要的系统操作记录不被删除
     * 
     * @param keepCount 保留的记录数量
     * @return 删除的记录数量
     */
    int cleanupOperateLogs(int keepCount);
    
    /**
     * 执行完整的日志清理
     * 使用默认的保留数量进行清理
     * 
     * @return 清理结果信息
     */
    String performFullCleanup();
    
    /**
     * 获取日志统计信息
     * 
     * @return 当前各类日志的数量统计
     */
    String getLogStatistics();
}