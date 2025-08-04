package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haibara
 * @description 分页返回
 * @since 2025/7/28 19:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "PageVO", description = "分页VO")
public class PageVO<T> {


    @Schema(description = "数据")
    private T page;

    @Schema(description = "数据总数")
    private Long total;
}