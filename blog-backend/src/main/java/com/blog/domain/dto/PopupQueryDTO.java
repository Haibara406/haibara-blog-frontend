package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author haibara
 * @description 弹窗查询条件数据传输对象
 * @since 2025/8/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "弹窗查询条件数据传输对象")
public class PopupQueryDTO implements BaseData {

    @Schema(description = "页码")
    @Min(value = 1, message = "页码必须大于0")
    private Long current;

    @Schema(description = "每页数量")
    @Min(value = 1, message = "每页数量必须大于0")
    private Long pageSize;

    @Schema(description = "弹窗标题（模糊搜索）")
    private String title;

    @Schema(description = "弹窗类型", allowableValues = {"1", "2", "3", "4"})
    private Integer popupType;

    @Schema(description = "显示位置", allowableValues = {"1", "2"})
    private Integer displayPosition;

    @Schema(description = "目标用户", allowableValues = {"1", "2", "3"})
    private Integer targetUsers;

    @Schema(description = "时间段", allowableValues = {"0", "1", "2", "3", "4"})
    private Integer timeRange;

    @Schema(description = "显示模式", allowableValues = {"1", "2", "3", "4"})
    private Integer displayMode;

    @Schema(description = "内容类型", allowableValues = {"1", "2", "3"})
    private Integer contentType;

    @Schema(description = "优先级权重（最小值）")
    @Min(value = 1, message = ValidationConstants.MIN_PRIORITY_RANGE_ERROR)
    private Integer minPriority;

    @Schema(description = "优先级权重（最大值）")
    @Min(value = 1, message = ValidationConstants.MAX_PRIORITY_RANGE_ERROR)
    private Integer maxPriority;

    @Schema(description = "是否可关闭", allowableValues = {"0", "1"})
    private Integer closeable;

    @Schema(description = "是否禁用", allowableValues = {"0", "1"})
    private Integer isDisable;

    @Schema(description = "创建时间开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "开始日期开始")
    private LocalDate startDateBegin;

    @Schema(description = "开始日期结束")
    private LocalDate startDateEnd;

    @Schema(description = "结束日期开始")
    private LocalDate endDateBegin;

    @Schema(description = "结束日期结束")
    private LocalDate endDateEnd;

    @Schema(description = "排序字段", allowableValues = {"priority", "createTime", "updateTime"})
    private String orderBy;

    @Schema(description = "排序方式", allowableValues = {"asc", "desc"})
    private String orderDirection;
}
