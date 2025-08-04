package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.entity.Server;
import com.blog.domain.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务监控控制器
 *
 * @author haibara
 * @description 服务监控接口
 * @since 2025/7/29 15:43
 */

@RestController
@Tag(name = "服务监控")
@RequestMapping("/monitor/server")
public class ServerController {

    /**
     * 获取服务监控数据
     * <p>
     * 实时获取服务器的系统监控信息，包括CPU使用率、内存使用情况、JVM状态、
     * 系统信息和磁盘使用情况。该接口用于系统监控页面显示服务器的运行状态。
     *
     * <p>返回的监控数据包含：
     * <ul>
     *     <li>CPU信息：核心数、总使用率、系统使用率、用户使用率、等待率、空闲率</li>
     *     <li>内存信息：总内存、已用内存、空闲内存、使用率（单位：GB）</li>
     *     <li>JVM信息：总内存、最大内存、空闲内存、版本、路径、运行时间等（单位：MB）</li>
     *     <li>系统信息：计算机名、IP地址、操作系统名称、架构、用户目录</li>
     *     <li>磁盘信息：各磁盘分区的总容量、已用容量、空闲容量、使用率</li>
     * </ul>
     *
     * @return 响应结果，包含服务器监控数据
     *         <ul>
     *             <li>成功时返回完整的服务器监控信息</li>
     *             <li>失败时返回错误信息，可能是因为系统信息获取失败</li>
     *         </ul>
     * @throws Exception 当获取系统信息时发生异常
     * @see Server 服务器信息实体类
     */
    @PreAuthorize("hasAnyAuthority('monitor:server:list')")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Operation(summary = "获取服务监控数据")
    @GetMapping()
    public ResponseResult<Server> getInfo() throws Exception {

        Server server = new Server();
        server.copyTo();
        return ResponseResult.success(server);
    }
}
