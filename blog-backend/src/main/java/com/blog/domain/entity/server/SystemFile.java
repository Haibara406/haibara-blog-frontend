package com.blog.domain.entity.server;

import lombok.Data;

/**
 * @author haibara
 * @description 系统文件相关信息
 * @since 2025/7/27 13:40
 */
@Data
public class SystemFile {
    /**
     * 盘符路径
     */
    private String dirName;

    /**
     * 盘符类型
     */
    private String sysTypeName;

    /**
     * 文件类型
     */
    private String typeName;

    /**
     * 总大小
     */
    private String total;

    /**
     * 剩余大小
     */
    private String free;

    /**
     * 已经使用量
     */
    private String used;

    /**
     * 资源的使用率
     */
    private double usage;

}
