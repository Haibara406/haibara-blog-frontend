package com.blog.domain.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author haibara
 * @description sys_permission表实体类
 * @since 2025/7/27 14:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_permission")
@Schema(description = "系统权限实体")
public class Permission implements BaseData {

    @Schema(description = "权限ID", example = "1")
    private Integer id;

    @Schema(description = "权限描述", example = "用户查询")
    private String permissionDesc;

    @Schema(description = "权限标识", example = "system:user:query")
    private String permissionKey;

    @Schema(description = "菜单ID", example = "1")
    private Long menuId;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

