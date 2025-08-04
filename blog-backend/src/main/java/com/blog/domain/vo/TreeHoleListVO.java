package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 后台树洞列表返回
 * @since 2025/7/31 11:54
 */
@Data
@Schema(description = "后台树洞列表返回")
public class TreeHoleListVO {

    @Schema(description = "树洞表id")
    private Long id;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "是否通过")
    private Integer isCheck;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;
}