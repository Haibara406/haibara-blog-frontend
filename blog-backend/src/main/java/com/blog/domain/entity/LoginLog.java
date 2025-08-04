package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.blog.domain.BaseData;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * @author haibara
 * @description sys_login_log表实体类
 * @since 2025/7/27 14:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_login_log")
@Schema(description = "登录日志实体")
public class LoginLog implements Serializable , BaseData {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "admin")
    private String userName;

    @Schema(description = "登录IP", example = "192.168.1.1")
    private String ip;

    @Schema(description = "登录地址", example = "北京市朝阳区")
    private String address;

    @Schema(description = "浏览器", example = "Chrome")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "登录类型：0-前台，1-后台，2-非法登录", example = "0", allowableValues = {"0", "1", "2"})
    private Integer type;

    @Schema(description = "登录状态：0-成功，1-失败", example = "0", allowableValues = {"0", "1"})
    private Integer state;

    @Schema(description = "登录信息", example = "登录成功")
    private String message;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

