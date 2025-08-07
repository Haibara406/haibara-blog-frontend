package com.blog.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author haibara
 * @description 弹窗视图对象
 * @since 2025/8/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "弹窗视图对象")
public class PopupVO {

    @Schema(description = "弹窗ID")
    private Long id;

    @Schema(description = "弹窗标题")
    private String title;

    @Schema(description = "弹窗内容")
    private String content;

    @Schema(description = "弹窗类型")
    private Integer popupType;

    @Schema(description = "弹窗类型描述")
    private String popupTypeDesc;

    @Schema(description = "显示位置")
    private Integer displayPosition;

    @Schema(description = "显示位置描述")
    private String displayPositionDesc;

    @Schema(description = "目标页面路径")
    private String targetPages;

    @Schema(description = "目标用户")
    private Integer targetUsers;

    @Schema(description = "目标用户描述")
    private String targetUsersDesc;

    @Schema(description = "时间段")
    private Integer timeRange;

    @Schema(description = "时间段描述")
    private String timeRangeDesc;

    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @Schema(description = "显示模式")
    private Integer displayMode;

    @Schema(description = "显示模式描述")
    private String displayModeDesc;

    @Schema(description = "内容类型")
    private Integer contentType;

    @Schema(description = "内容类型描述")
    private String contentTypeDesc;

    @Schema(description = "弹窗图片URL")
    private String imageUrl;

    @Schema(description = "跳转链接")
    private String jumpUrl;

    @Schema(description = "弹窗位置")
    private String popupPosition;

    @Schema(description = "弹窗位置描述")
    private String popupPositionDesc;

    @Schema(description = "优先级权重")
    private Integer priority;

    @Schema(description = "用户是否可关闭")
    private Integer closeable;

    @Schema(description = "是否可关闭描述")
    private String closeableDesc;

    @Schema(description = "自动关闭时间(秒)")
    private Integer autoCloseTime;

    @Schema(description = "是否禁用")
    private Integer isDisable;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "是否在有效期内")
    private Boolean isInValidPeriod;

    @Schema(description = "是否在时间段内")
    private Boolean isInTimeRange;
}
