package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author haibara
 * @description 删除角色请求
 * @since 2025/7/28 23:36
 */
@Data
@Schema(description = "删除角色请求")
public class RoleDeleteDTO {

    @NotNull(message = ValidationConstants.ROLE_ID_NOT_NULL)
    @Schema(description = "角色id列表")
    private List<Long> Ids;
}
