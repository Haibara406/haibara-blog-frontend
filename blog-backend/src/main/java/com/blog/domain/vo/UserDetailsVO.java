package com.blog.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 用户详情返回
 * @since 2025/7/28 00:17
 */
@Data
@Schema(description = "用户详情返回")
public class UserDetailsVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户密码", hidden = true)
    private String password;

    @Schema(description = "用户角色")
    private List<String> roles;
    @Schema(description = "用户性别：0-未定义，1-男，2-女", allowableValues = {"0", "1", "2"})
    private Integer gender;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "个人简介")
    private String intro;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户注册方式：0-邮箱/用户名，1-Gitee，2-Github", allowableValues = {"0", "1", "2"})
    private Integer registerType;

    @Schema(description = "用户注册IP")
    private String registerIp;

    @Schema(description = "用户注册地址")
    private String registerAddress;

    @Schema(description = "用户登录方式：0-邮箱/用户名，1-Gitee，2-Github", allowableValues = {"0", "1", "2"})
    private Integer loginType;

    @Schema(description = "用户登录IP")
    private String loginIp;

    @Schema(description = "登录地址")
    private String loginAddress;

    @Schema(description = "是否禁用：0-否，1-是", allowableValues = {"0", "1"})
    private Integer isDisable;

    @Schema(description = "用户最近登录时间")
    private Date loginTime;

    @Schema(description = "用户创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "用户更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

