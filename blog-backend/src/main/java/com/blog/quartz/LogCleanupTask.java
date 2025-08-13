package com.blog.quartz;

import com.blog.service.LogCleanupService;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 日志清理定时任务
 * 
 * @author haibara
 * @description 定期清理登录日志和操作日志，保持数据库性能
 * @since 2025/8/13
 */
@Slf4j
public class LogCleanupTask extends QuartzJobBean {
    
    @Resource
    private LogCleanupService logCleanupService;
    
    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        log.info("===============================");
        log.info("开始执行日志清理定时任务");
        log.info("===============================");
        
        try {
            // 执行日志清理
            String result = logCleanupService.performFullCleanup();
            log.info("日志清理任务执行成功: {}", result);
            
        } catch (Exception e) {
            log.error("日志清理定时任务执行失败", e);
            // 不抛出异常，避免影响其他定时任务
        } finally {
            log.info("===============================");
            log.info("日志清理定时任务执行结束");
            log.info("===============================");
        }
    }
}