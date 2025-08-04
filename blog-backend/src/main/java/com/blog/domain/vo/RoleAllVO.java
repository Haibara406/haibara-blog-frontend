package com.blog.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 角色列表返回
 * @since 2025/7/28 23:26
 */
@Data
@Schema(description = "角色列表返回")
public class RoleAllVO {

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

    @Schema(description = "创建时间")
    private Date createTime;
}
