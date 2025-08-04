package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 搜索操作日志请求
 * @since 2025/7/28 19:31
 */
@Data
@Schema
public class LogDTO {

    @Schema(description = "ip地址")
    private String ip;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作人员")
    private String userName;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "操作状态(0：成功，1：失败)")
    private Integer state;

    @Schema(description = "操作时间开始")
    private Date logTimeStart;

    @Schema(description = "操作时间结束")
    private Date logTimeEnd;

    @Schema(description = "当前页")
    @NotNull(message = ValidationConstants.CURRENT_PAGE_NOT_NULL)
    private Long current;

    @Schema(description = "每页数量")
    @NotNull(message =  ValidationConstants.PAGE_SIZE_NOT_NULL)
    private Long pageSize;
}
