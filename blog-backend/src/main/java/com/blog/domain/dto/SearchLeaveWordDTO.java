package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 留言搜素请求
 * @since 2025/7/31 14:37
 */
@Data
@Schema(description = "留言搜素请求")
public class SearchLeaveWordDTO {

    @Schema(description = "留言用户")
    private String userName;

    @Schema(description = "是否通过 (0否 1是)")
    private Integer isCheck;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
