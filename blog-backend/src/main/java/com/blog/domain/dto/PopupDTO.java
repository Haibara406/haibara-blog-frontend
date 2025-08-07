package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author haibara
 * @description 弹窗数据传输对象
 * @since 2025/8/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "弹窗数据传输对象")
public class PopupDTO implements BaseData {

    @Schema(description = "弹窗ID（修改时必填）")
    private Long id;

    @Schema(description = "弹窗标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = ValidationConstants.POPUP_TITLE_NOT_NULL)
    @Size(max = 100, message = ValidationConstants.POPUP_TITLE_TOO_LONG)
    private String title;

    @Schema(description = "弹窗内容")
    @Size(max = 65535, message = ValidationConstants.POPUP_CONTENT_TOO_LONG)
    private String content;

    @Schema(description = "弹窗类型", allowableValues = {"1", "2", "3", "4"})
    @NotNull(message = ValidationConstants.POPUP_TYPE_NOT_NULL)
    @Min(value = 1, message = ValidationConstants.POPUP_TYPE_RANGE_ERROR)
    @Max(value = 4, message = ValidationConstants.POPUP_TYPE_RANGE_ERROR)
    private Integer popupType;

    @Schema(description = "显示位置", allowableValues = {"1", "2"})
    @NotNull(message = ValidationConstants.DISPLAY_POSITION_NOT_NULL)
    @Min(value = 1, message = ValidationConstants.DISPLAY_POSITION_RANGE_ERROR)
    @Max(value = 2, message = ValidationConstants.DISPLAY_POSITION_RANGE_ERROR)
    private Integer displayPosition;

    @Schema(description = "目标页面路径")
    @Size(max = 500, message = ValidationConstants.TARGET_PAGES_TOO_LONG)
    private String targetPages;

    @Schema(description = "目标用户", allowableValues = {"1", "2", "3"})
    @NotNull(message = ValidationConstants.TARGET_USERS_NOT_NULL)
    @Min(value = 1, message = ValidationConstants.TARGET_USERS_RANGE_ERROR)
    @Max(value = 3, message = ValidationConstants.TARGET_USERS_RANGE_ERROR)
    private Integer targetUsers;

    @Schema(description = "时间段", allowableValues = {"0", "1", "2", "3", "4"})
    @NotNull(message = ValidationConstants.TIME_RANGE_NOT_NULL)
    @Min(value = 0, message = ValidationConstants.TIME_RANGE_RANGE_ERROR)
    @Max(value = 4, message = ValidationConstants.TIME_RANGE_RANGE_ERROR)
    private Integer timeRange;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "显示模式", allowableValues = {"1", "2", "3", "4"})
    @NotNull(message = ValidationConstants.DISPLAY_MODE_NOT_NULL)
    @Min(value = 1, message = ValidationConstants.DISPLAY_MODE_RANGE_ERROR)
    @Max(value = 4, message = ValidationConstants.DISPLAY_MODE_RANGE_ERROR)
    private Integer displayMode;

    @Schema(description = "内容类型", allowableValues = {"1", "2", "3"})
    @NotNull(message = ValidationConstants.CONTENT_TYPE_NOT_NULL)
    @Min(value = 1, message = ValidationConstants.CONTENT_TYPE_RANGE_ERROR)
    @Max(value = 3, message = ValidationConstants.CONTENT_TYPE_RANGE_ERROR)
    private Integer contentType;

    @Schema(description = "弹窗图片URL")
    @Size(max = 500, message = ValidationConstants.IMAGE_URL_TOO_LONG)
    private String imageUrl;

    @Schema(description = "跳转链接")
    @Size(max = 500, message = ValidationConstants.JUMP_URL_TOO_LONG)
    private String jumpUrl;

    @Schema(description = "弹窗位置", allowableValues = {"center", "top", "bottom", "left", "right"})
    @Size(max = 20, message = ValidationConstants.POPUP_POSITION_TOO_LONG)
    private String popupPosition;

    @Schema(description = "优先级权重")
    @NotNull(message = ValidationConstants.PRIORITY_NOT_NULL)
    @Min(value = 1, message = ValidationConstants.PRIORITY_RANGE_ERROR)
    @Max(value = 99, message = ValidationConstants.PRIORITY_RANGE_ERROR)
    private Integer priority;

    @Schema(description = "用户是否可关闭", allowableValues = {"0", "1"})
    @NotNull(message = ValidationConstants.CLOSEABLE_NOT_NULL)
    @Min(value = 0, message = ValidationConstants.CLOSEABLE_RANGE_ERROR)
    @Max(value = 1, message = ValidationConstants.CLOSEABLE_RANGE_ERROR)
    private Integer closeable;

    @Schema(description = "自动关闭时间(秒)")
    @Min(value = 1, message = ValidationConstants.AUTO_CLOSE_TIME_RANGE_ERROR)
    private Integer autoCloseTime;

    @Schema(description = "是否禁用", allowableValues = {"0", "1"})
    private Integer isDisable;
}
