package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 搜索收藏列表请求
 * @since 2025/7/31 21:17
 */
@Data
@Schema(description = "搜索收藏列表请求")
public class SearchFavoriteDTO {

    @Schema(description = "收藏的用户姓名")
    private String userName;

    @Schema(description = "收藏类型(1,文章 2,留言板)")
    private Integer type;

    @Schema(description = "是否有效 (0否 1是)")
    private Integer isCheck;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
