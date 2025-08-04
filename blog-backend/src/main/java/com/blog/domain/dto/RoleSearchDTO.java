package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 角色搜素条件请求
 * @since 2025/7/28 23:36
 */
@Data
@Schema(description = "角色搜素条件请求")
public class RoleSearchDTO {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标识")
    private String roleKey;

    @Schema(description = "状态：0-正常，1-停用", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "创建时间开始")
    private Date createTimeStart;

    @Schema(description = "创建时间结束")
    private Date createTimeEnd;
}

