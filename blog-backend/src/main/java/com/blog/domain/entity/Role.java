package com.blog.domain.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.blog.domain.BaseData;

import java.util.Date;


/**
 * @author haibara
 * @description sys_role表实体类
 * @since 2025/7/27 14:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("sys_role")
@Schema(description = "系统角色实体")
public class Role implements BaseData {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "角色标识", example = "admin")
    private String roleKey;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;

    @Schema(description = "状态：0-正常，1-停用", example = "0", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "排序号", example = "1")
    private Long orderNum;

    @Schema(description = "备注", example = "系统管理员角色")
    private String remark;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

