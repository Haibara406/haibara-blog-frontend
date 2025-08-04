package com.blog.domain.entity.server;

import lombok.Data;

/**
 * @author haibara
 * @description 系统相关信息
 * @since 2025/7/27 13:40
 */
@Data
public class System {
    /**
     * 服务器名称
     */
    private String computerName;

    /**
     * 服务器Ip
     */
    private String computerIp;

    /**
     * 项目路径
     */
    private String userDir;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 系统架构
     */
    private String osArch;

}
