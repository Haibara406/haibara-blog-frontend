package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 所有通过申请的友链返回
 * @since 2025/8/1 13:16
 */
@Data
@Schema(description = "所有通过申请的友链返回")
public class LinkVO {

    @Schema(description = "友链id")
    private Long id;

    @Schema(description = "网站名称")
    private String name;

    @Schema(description = "网站地址")
    private String url;

    @Schema(description = "网站描述")
    private String description;

    @Schema(description = "网站背景")
    private String background;

    @Schema(description = "用户头像")
    private String avatar;
}