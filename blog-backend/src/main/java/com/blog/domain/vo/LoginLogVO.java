package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author haibara
 * @description 登录日志返回信息
 * @since 2025/7/28 15:15
 */
@Data
@Schema(description = "登录日志返回信息")
public class LoginLogVO implements Serializable {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "登录IP")
    private String ip;

    @Schema(description = "登录地址")
    private String address;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录类型：0-前台，1-后台，2-非法登录", allowableValues = {"0", "1", "2"})
    private Integer type;

    @Schema(description = "登录状态：0-成功，1-失败", allowableValues = {"0", "1"})
    private Integer state;

    @Schema(description = "登录信息")
    private String message;

    @Schema(description = "用户登录时间")
    private Date loginTime;
}
