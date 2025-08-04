package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author haibara
 * @description 树洞返回
 * @since 2025/7/31 11:53
 */
@Data
@Schema(description = "树洞返回")
public class TreeHoleVO {


    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "内容")
    private String content;
}