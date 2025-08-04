package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.blog.domain.BaseData;

import java.util.Date;


/**
 * @author haibara
 * @description t_tree_hole表实体类
 * @since 2025/7/27 14:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_tree_hole")
@Schema(description = "树洞实体")
public class TreeHole implements BaseData {

    @Schema(description = "树洞ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "树洞内容", example = "这是一条匿名的树洞消息")
    private String content;

    @Schema(description = "是否通过审核：0-否，1-是", example = "1", allowableValues = {"0", "1"})
    private Integer isCheck;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "修改时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

