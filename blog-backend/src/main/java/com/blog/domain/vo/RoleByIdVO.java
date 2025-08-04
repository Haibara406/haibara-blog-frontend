package com.blog.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 修改角色的信息返回
 * @since 2025/7/28 23:30
 */
@Data
@Schema(description = "修改角色的信息返回")
public class RoleByIdVO {
    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标识")
    private String roleKey;

    @Schema(description = "状态：0-正常，1-停用", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "排序号")
    private Long orderNum;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "角色拥有的菜单权限")
    private List<Long> menuIds;
}
