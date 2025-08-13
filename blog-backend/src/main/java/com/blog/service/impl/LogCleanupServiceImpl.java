package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.constants.ErrorConst;
import com.blog.domain.entity.Log;
import com.blog.mapper.LogMapper;
import com.blog.mapper.LoginLogMapper;
import com.blog.service.LogCleanupService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 日志清理服务实现
 * 
 * @author haibara
 * @description 系统日志自动清理功能的具体实现
 * @since 2025/8/13
 */
@Slf4j
@Service
public class LogCleanupServiceImpl implements LogCleanupService {
    
    @Resource
    private LoginLogMapper loginLogMapper;
    
    @Resource
    private LogMapper logMapper;
    
    /**
     * 默认保留的登录日志数量
     */
    @Value("${log.cleanup.login-log.keep-count:5000}")
    private int defaultLoginLogKeepCount;
    
    /**
     * 默认保留的操作日志数量
     */
    @Value("${log.cleanup.operate-log.keep-count:8000}")
    private int defaultOperateLogKeepCount;
    
    /**
     * 受保护操作日志的总体上限
     */
    @Value("${log.cleanup.operate-log.protected-keep-count:20000}")
    private int protectedLogKeepCount;
    
    /**
     * 受保护的操作类型（不会被自动清理）
     */
    private static final List<String> PROTECTED_OPERATIONS = Arrays.asList(
            "登录", "注册", "修改密码", "权限变更", "角色分配", "系统配置"
    );
    
    /**
     * 清理登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupLoginLogs(int keepCount) {
        try {
            log.info("开始清理登录日志，保留最新 {} 条记录", keepCount);
            
            // 1. 查询当前登录日志总数
            Long totalCount = loginLogMapper.selectCount(null);
            if (totalCount <= keepCount) {
                log.info("当前登录日志数量 {} 未超过保留数量 {}，无需清理", totalCount, keepCount);
                return 0;
            }
            
            // 2. 计算需要删除的数量
            long deleteCount = totalCount - keepCount;
            log.info("当前登录日志总数 {}，需要删除最旧的 {} 条记录", totalCount, deleteCount);
            
            // 3. 查询需要删除的记录ID（最旧的记录）
            List<Long> idsToDelete = loginLogMapper.selectOldestIds(deleteCount);
            
            if (idsToDelete.isEmpty()) {
                log.warn("未找到需要删除的登录日志记录");
                return 0;
            }
            
            // 4. 执行批量删除
            int deletedCount = loginLogMapper.deleteBatchIds(idsToDelete);
            log.info("成功删除 {} 条登录日志记录", deletedCount);
            
            return deletedCount;
            
        } catch (Exception e) {
            log.error(ErrorConst.LOGIN_LOG_CLEANUP_ERROR, e);
            throw new RuntimeException(ErrorConst.LOGIN_LOG_CLEANUP_FAILED + e.getMessage());
        }
    }
    
    /**
     * 清理操作日志
     * 实现双重限制策略：
     * 1. 先清理普通操作日志到指定数量
     * 2. 如果总日志数仍超过受保护日志上限，则清理最旧的受保护日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupOperateLogs(int keepCount) {
        try {
            log.info("开始清理操作日志，普通日志保留 {} 条，受保护日志上限 {} 条", keepCount, protectedLogKeepCount);
            
            int totalDeletedCount = 0;
            
            // 第一阶段：清理普通操作日志
            totalDeletedCount += cleanupNormalOperateLogs(keepCount);
            
            // 第二阶段：检查并清理超出上限的受保护日志
            totalDeletedCount += cleanupExcessProtectedLogs();
            
            log.info("操作日志清理完成，总共删除 {} 条记录", totalDeletedCount);
            return totalDeletedCount;
            
        } catch (Exception e) {
            log.error(ErrorConst.OPERATE_LOG_CLEANUP_ERROR, e);
            throw new RuntimeException(ErrorConst.OPERATE_LOG_CLEANUP_FAILED + e.getMessage());
        }
    }
    
    /**
     * 清理普通操作日志（非受保护）
     */
    private int cleanupNormalOperateLogs(int keepCount) {
        // 1. 查询非保护操作的日志总数
        LambdaQueryWrapper<Log> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.notIn(Log::getOperation, PROTECTED_OPERATIONS);
        Long normalLogCount = logMapper.selectCount(countWrapper);
        
        if (normalLogCount <= keepCount) {
            log.info("当前普通操作日志数量 {} 未超过保留数量 {}，无需清理", normalLogCount, keepCount);
            return 0;
        }
        
        // 2. 计算需要删除的数量
        long deleteCount = normalLogCount - keepCount;
        log.info("当前普通操作日志总数 {}，需要删除最旧的 {} 条记录", normalLogCount, deleteCount);
        
        // 3. 查询需要删除的记录ID（最旧的非保护记录）
        List<Long> idsToDelete = logMapper.selectOldestNonProtectedIds(PROTECTED_OPERATIONS, deleteCount);
        
        if (idsToDelete.isEmpty()) {
            log.warn("未找到需要删除的普通操作日志记录");
            return 0;
        }
        
        // 4. 执行批量删除
        int deletedCount = logMapper.deleteBatchIds(idsToDelete);
        log.info("成功删除 {} 条普通操作日志记录", deletedCount);
        
        return deletedCount;
    }
    
    /**
     * 清理超出上限的受保护日志
     */
    private int cleanupExcessProtectedLogs() {
        // 1. 查询受保护操作的日志总数
        LambdaQueryWrapper<Log> protectedWrapper = new LambdaQueryWrapper<>();
        protectedWrapper.in(Log::getOperation, PROTECTED_OPERATIONS);
        Long protectedLogCount = logMapper.selectCount(protectedWrapper);
        
        if (protectedLogCount <= protectedLogKeepCount) {
            log.info("当前受保护日志数量 {} 未超过上限 {}，无需清理", protectedLogCount, protectedLogKeepCount);
            return 0;
        }
        
        // 2. 计算需要删除的受保护日志数量
        long deleteCount = protectedLogCount - protectedLogKeepCount;
        log.info("当前受保护日志总数 {}，超过上限 {}，需要删除最旧的 {} 条记录", 
                protectedLogCount, protectedLogKeepCount, deleteCount);
        
        // 3. 查询需要删除的受保护记录ID
        List<Long> idsToDelete = logMapper.selectOldestProtectedIds(PROTECTED_OPERATIONS, deleteCount);
        
        if (idsToDelete.isEmpty()) {
            log.warn("未找到需要删除的受保护操作日志记录");
            return 0;
        }
        
        // 4. 执行批量删除
        int deletedCount = logMapper.deleteBatchIds(idsToDelete);
        log.info("成功删除 {} 条受保护操作日志记录", deletedCount);
        
        return deletedCount;
    }
    
    /**
     * 执行完整的日志清理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String performFullCleanup() {
        try {
            log.info("========== 开始执行自动日志清理任务 ==========");
            String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 记录清理前的统计信息
            String beforeStats = getLogStatistics();
            log.info("清理前统计: {}", beforeStats);
            
            // 清理登录日志
            int deletedLoginLogs = cleanupLoginLogs(defaultLoginLogKeepCount);
            
            // 清理操作日志
            int deletedOperateLogs = cleanupOperateLogs(defaultOperateLogKeepCount);
            
            // 记录清理后的统计信息
            String afterStats = getLogStatistics();
            log.info("清理后统计: {}", afterStats);
            
            String result = String.format(
                "日志自动清理完成 [%s] - 删除登录日志: %d条, 删除操作日志: %d条",
                startTime, deletedLoginLogs, deletedOperateLogs
            );
            
            log.info("========== 日志清理任务执行完成 ==========");
            log.info("清理结果: {}", result);
            
            return result;
            
        } catch (Exception e) {
            String errorMsg = String.format(ErrorConst.LOG_CLEANUP_TASK_FAILED + "%s", e.getMessage());
            log.error(errorMsg, e);
            return errorMsg;
        }
    }
    
    /**
     * 获取日志统计信息
     */
    @Override
    public String getLogStatistics() {
        try {
            // 登录日志统计
            Long loginLogCount = loginLogMapper.selectCount(null);
            
            // 操作日志统计
            Long operateLogCount = logMapper.selectCount(null);
            
            // 受保护的操作日志统计
            LambdaQueryWrapper<Log> protectedWrapper = new LambdaQueryWrapper<>();
            protectedWrapper.in(Log::getOperation, PROTECTED_OPERATIONS);
            Long protectedLogCount = logMapper.selectCount(protectedWrapper);
            
            return String.format(
                "登录日志: %d条, 操作日志: %d条 (其中受保护: %d条)",
                loginLogCount, operateLogCount, protectedLogCount
            );
            
        } catch (Exception e) {
            log.error(ErrorConst.GET_LOG_STATISTICS_FAILED, e);
            return ErrorConst.LOG_STATISTICS_GET_FAILED + e.getMessage();
        }
    }
}