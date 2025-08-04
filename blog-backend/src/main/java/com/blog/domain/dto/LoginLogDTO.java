package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 搜索登录日志请求
 * @since 2025/7/28 15:19
 */

@Data
public class LoginLogDTO {

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "登录地址")
    private String address;

    @Schema(description = "登录状态(0：成功，1：失败)")
    private Integer state;

    @Schema(description = "登录开始时间")
    private Date loginTimeStart;

    @Schema(description = "登录结束时间")
    private Date loginTimeEnd;
}
