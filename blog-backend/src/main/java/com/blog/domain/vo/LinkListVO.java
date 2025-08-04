package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 友链列表返回
 * @since 2025/8/1 13:17
 */
@Data
@Schema(description = "友链列表返回")
public class LinkListVO {

    @Schema(description = "友链id")
    private Long id;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "网站名称")
    private String name;

    @Schema(description = "网站地址")
    private String url;

    @Schema(description = "网站描述")
    private String description;

    @Schema(description = "网站背景")
    private String background;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "审核状态（0：未通过，1：已通过）")
    private Integer isCheck;
}
