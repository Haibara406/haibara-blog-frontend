package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 日志数据返回
 * @since 2025/7/28 19:43
 */
@Data
@Schema(description = "日志数据返回")
public class LogVO  {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作名称")
    private String operation;

    @Schema(description = "操作人员")
    private String userName;

    @Schema(description = "操作IP地址")
    private String ip;

    @Schema(description = "操作地点")
    private String address;

    @Schema(description = "操作状态：0-成功，1-失败，2-异常", allowableValues = {"0", "1", "2"})
    private Integer state;

    @Schema(description = "操作方法")
    private String method;

    @Schema(description = "请求方式")
    private String reqMapping;

    @Schema(description = "请求参数")
    private String reqParameter;

    @Schema(description = "异常信息")
    private String exception;

    @Schema(description = "返回参数")
    private String returnParameter;

    @Schema(description = "请求地址")
    private String reqAddress;

    @Schema(description = "消耗时间（毫秒）")
    private Long time;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "登录时间")
    private Date loginTime;
}
