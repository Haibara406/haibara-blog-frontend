package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 用户账户VO
 * @since 2025/7/27 21:31
 */

@Data
@Schema(name = "UserAccountVO", description = "前台用户账户VO")
public class UserAccountVO {

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户注册方式：0-邮箱/用户名，1-Gitee，2-Github", allowableValues = {"0", "1", "2"})
    private Integer registerType;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "个人简介")
    private String intro;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户性别：0-未定义，1-男，2-女", allowableValues = {"0", "1", "2"})
    private Integer gender;

    @Schema(description = "用户角色")
    private List<String> roles;

    @Schema(description = "用户权限")
    private List<String> permissions;

    @Schema(description = "用户最近登录时间")
    private Date loginTime;

    @Schema(description = "用户创建时间")
    private Date createTime;
}
