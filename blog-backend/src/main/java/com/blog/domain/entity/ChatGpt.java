package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description t_chat_gpt表实体类
 * @since 2025/7/27 14:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_chat_gpt")
@Schema(description = "ChatGPT对话实体")
public class ChatGpt implements BaseData {

    @Schema(description = "对话ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "会话记录（JSON格式）", example = "{\"messages\":[{\"role\":\"user\",\"content\":\"你好\"}]}")
    private String conversation;

    @Schema(description = "是否有效：0-否，1-是", example = "1", allowableValues = {"0", "1"})
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
