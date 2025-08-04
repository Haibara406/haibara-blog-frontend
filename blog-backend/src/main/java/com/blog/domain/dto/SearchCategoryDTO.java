package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 搜索分类请求
 * @since 2025/7/30 19:26
 */
@Data
@Schema(description = "搜索分类请求")
public class SearchCategoryDTO {

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}

