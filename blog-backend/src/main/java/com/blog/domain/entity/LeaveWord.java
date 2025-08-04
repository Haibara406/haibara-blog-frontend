package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.blog.domain.BaseData;

import java.util.Date;


/**
 * @author haibara
 * @description t_leave_word表实体类
 * @since 2025/7/27 14:03
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_leave_word")
@Schema(description = "留言实体")
public class LeaveWord implements BaseData {

    @Schema(description = "留言ID", example = "1")
    private Long id;

    @Schema(description = "留言用户ID", example = "1001")
    private Long userId;

    @Schema(description = "留言内容", example = "这是一条留言")
    private String content;

    @Schema(description = "是否通过审核：0-否，1-是", example = "1", allowableValues = {"0", "1"})
    private Integer isCheck;

    @Schema(description = "留言时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

