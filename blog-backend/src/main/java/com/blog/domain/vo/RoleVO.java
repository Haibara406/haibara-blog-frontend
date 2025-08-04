package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 角色信息返回
 * @since 2025/7/28 23:40
 */
@Data
@Schema(description = "角色信息返回")
public class RoleVO {

    @Schema(description = "角色id")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;
}
