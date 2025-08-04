package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 标签搜索请求
 * @since 2025/7/30 21:37
 */
@Data
@Schema(description = "标签搜索请求")
public class SearchTagDTO{

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}