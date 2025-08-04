package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haibara
 * @description 查询黑名单请求
 * @since 2025/7/28 16:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "查询黑名单请求")
public class SearchBlackListDTO {

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "封禁理由")
    private String reason;

    @Schema(description = "封禁类型")
    private Integer type;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
