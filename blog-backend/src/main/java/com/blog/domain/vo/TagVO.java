package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 标签返回
 * @since 2025/7/29 17:52
 */
@Data
@Schema(name = "TagVO", description = "标签VO")
public class TagVO {

    @Schema(description = "标签id")
    private Long id;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "标签下的文章")
    private Long articleCount;

    @Schema(description = "标签创建时间")
    private Date createTime;

    @Schema(description = "标签更新时间")
    private Date updateTime;
}