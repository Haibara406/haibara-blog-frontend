package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author haibara
 * @description 添加或修改角色信息请求
 * @since 2025/7/28 23:32
 */
@Data
@Accessors(chain = true)
@Schema(description = "添加或修改角色信息请求")
public class RoleDTO implements BaseData {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    @NotNull(message = ValidationConstants.ROLE_NAME_NOT_NULL)
    private String roleName;

    @Schema(description = "角色标识")
    @NotNull(message = ValidationConstants.ROLE_KEY_NOT_NULL)
    private String roleKey;

    @Schema(description = "状态：0-正常，1-停用", allowableValues = {"0", "1"})
    @NotNull(message = ValidationConstants.STATUS_NOT_NULL)
    private Integer status;

    @Schema(description = "排序号")
    @NotNull(message =  ValidationConstants.ORDER_NUM_NOT_NULL)
    private Long orderNum;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "权限菜单id")
    private List<Long> menuIds;
}
