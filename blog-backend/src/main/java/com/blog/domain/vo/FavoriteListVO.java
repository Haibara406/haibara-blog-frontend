package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 收藏列表返回
 * @since 2025/7/31 21:16
 */
@Data
@Schema(description = "收藏列表返回")
public class FavoriteListVO {

    @Schema(description = "收藏id")
    private Long id;

    @Schema(description = "收藏的用户姓名")
    private String userName;

    @Schema(description = "收藏类型(1,文章 2,留言板)")
    private Integer type;

    @Schema(description = "收藏内容")
    private String content;

    @Schema(description = "是否有效 (0否 1是)")
    private Integer isCheck;

    @Schema(description = "收藏时间")
    private Date createTime;
}