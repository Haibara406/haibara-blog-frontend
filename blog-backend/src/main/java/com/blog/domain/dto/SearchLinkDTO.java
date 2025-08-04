package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 友链搜索请求
 * @since 2025/8/1 13:19
 */
@Data
@Schema(description = "友链搜索请求")
public class SearchLinkDTO {

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "网站名称")
    private String name;

    @Schema(description = "是否通过 (0否 1是)")
    private Integer isCheck;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
