package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.blog.domain.BaseData;
import com.blog.domain.ip.BlackListIpInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description t_black_list表实体类
 * @since 2025/7/27 13:59
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_black_list", autoResultMap = true)
@Schema(description = "黑名单实体")
public class BlackList implements BaseData {

    @Schema(description = "黑名单ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "封禁理由", example = "违规发言")
    private String reason;

    @Schema(description = "封禁时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date bannedTime;

    @Schema(description = "到期时间", example = "2025-08-27 14:30:00")
    private Date expiresTime;

    @Schema(description = "类型：1-用户，2-路人/攻击者", example = "1", allowableValues = {"1", "2"})
    private Integer type;

    @Schema(description = "IP信息（当type=2时需要）")
    @TableField(value = "ip_info",typeHandler = JacksonTypeHandler.class)
    private BlackListIpInfo ipInfo;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除（0：未删除，1：已删除）默认：0", allowableValues = {"0", "1"}, defaultValue = "0")
    private Integer isDeleted;
}