package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.blog.domain.BaseData;

import java.util.Date;


/**
 * @author haibara
 * @description sys_user表实体类
 * @since 2025/7/27 14:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("sys_user")
@Schema(description = "用户实体")
public class User implements BaseData {
    @Schema(description = "用户ID", example = "1001")
    private Long id;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "用户密码", hidden = true)
    private String password;

    @Schema(description = "用户性别：0-未定义，1-男，2-女", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;

    @Schema(description = "用户头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "个人简介", example = "这是一个简介")
    private String intro;

    @Schema(description = "用户邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "用户注册方式：0-邮箱/用户名，1-Gitee，2-Github", example = "0", allowableValues = {"0", "1", "2"})
    private Integer registerType;

    @Schema(description = "用户注册IP", example = "192.168.1.1")
    private String registerIp;

    @Schema(description = "用户注册地址", example = "北京市朝阳区")
    private String registerAddress;

    @Schema(description = "用户登录方式：0-邮箱/用户名，1-Gitee，2-Github", example = "0", allowableValues = {"0", "1", "2"})
    private Integer loginType;

    @Schema(description = "用户登录IP", example = "192.168.1.1")
    private String loginIp;

    @Schema(description = "登录地址", example = "北京市朝阳区")
    private String loginAddress;

    @Schema(description = "是否禁用：0-否，1-是", example = "0", allowableValues = {"0", "1"})
    private Integer isDisable;

    @Schema(description = "用户最近登录时间", example = "2025-07-27 14:30:00")
    private Date loginTime;

    @Schema(description = "用户创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "用户更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

