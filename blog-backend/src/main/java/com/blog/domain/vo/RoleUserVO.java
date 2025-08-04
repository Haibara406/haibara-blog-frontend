package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 用户角色返回
 * @since 2025/7/29 14:55
 */
@Data
@Schema(description = "用户角色返回")
public class RoleUserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "是否禁用：0-否，1-是", allowableValues = {"0", "1"})
    private Integer isDisable;

    @Schema(description = "用户创建时间")
    private Date createTime;
}