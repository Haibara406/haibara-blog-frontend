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
 * @description sys_log表实体类
 * @since 2025/7/27 14:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "sys_log")
@Schema(description = "系统操作日志实体")
public class Log implements Serializable, BaseData {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "模块名称", example = "用户管理")
    private String module;

    @Schema(description = "操作名称", example = "新增用户")
    private String operation;

    @Schema(description = "操作人员", example = "admin")
    private String userName;

    @Schema(description = "操作IP地址", example = "192.168.1.1")
    private String ip;

    @Schema(description = "操作地点", example = "北京市朝阳区")
    private String address;

    @Schema(description = "操作状态：0-成功，1-失败，2-异常", example = "0", allowableValues = {"0", "1", "2"})
    private Integer state;

    @Schema(description = "操作方法", example = "com.blog.controller.UserController.addUser")
    private String method;

    @Schema(description = "请求方式", example = "POST")
    private String reqMapping;

    @Schema(description = "请求参数", example = "{\"username\":\"test\",\"email\":\"test@example.com\"}")
    private String reqParameter;

    @Schema(description = "异常信息", example = "java.lang.NullPointerException: ...")
    private String exception;

    @Schema(description = "返回参数", example = "{\"code\":200,\"msg\":\"success\",\"data\":{}}")
    private String returnParameter;

    @Schema(description = "请求地址", example = "/api/user/add")
    private String reqAddress;

    @Schema(description = "消耗时间（毫秒）", example = "150")
    private Long time;

    @Schema(description = "操作描述", example = "新增用户操作")
    private String description;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

