package com.blog.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author haibara
 * @description 日志统计信息详细返回对象
 * @since 2025/1/15
 */
@Data
@Schema(description = "日志统计信息")
public class LogStatisticsVO {
    
    // 基础统计
    @Schema(description = "登录日志数量", example = "1250")
    private Long loginLogCount;
    
    @Schema(description = "操作日志数量", example = "3500")
    private Long operateLogCount;
    
    @Schema(description = "受保护日志数量", example = "850")
    private Long protectedLogCount;
    
    @Schema(description = "非保护日志数量", example = "2650")
    private Long unprotectedLogCount;
    
    @Schema(description = "总日志数量", example = "4750")
    private Long totalLogCount;
    
    // 时间信息 - 只保留最后清理时间
    @Schema(description = "最后清理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCleanupTime;
    
    // 存储信息 - 简单估算
    @Schema(description = "预估存储大小", example = "3.2 MB")
    private String estimatedSize;
    
    // 详细分类统计
    @Schema(description = "受保护操作统计")
    private Map<String, Long> protectedOperationStats;
    
    @Schema(description = "非保护操作统计")
    private Map<String, Long> unprotectedOperationStats;
    
    // 操作类型说明
    @Schema(description = "受保护操作类型列表")
    private List<String> protectedOperations;
    
    @Schema(description = "非保护操作类型列表")
    private List<String> unprotectedOperations;
}