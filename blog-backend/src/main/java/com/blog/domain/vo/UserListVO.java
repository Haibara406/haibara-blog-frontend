package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 返回用户列表
 * @since 2025/7/27 23:42
 */
@Data
@Schema(description = "返回用户列表")
public class UserListVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户注册方式：0-邮箱/用户名，1-Gitee，2-Github", allowableValues = {"0", "1", "2"})
    private Integer registerType;

    @Schema(description = "登录地址")
    private String loginAddress;

    @Schema(description = "是否禁用：0-否，1-是", allowableValues = {"0", "1"})
    private Integer isDisable;

    @Schema(description = "用户创建时间")
    private Date createTime;
}
