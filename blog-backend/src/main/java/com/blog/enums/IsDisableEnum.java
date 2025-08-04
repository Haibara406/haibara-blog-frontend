package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 弃用状态枚举
 * @since 2025/8/2 13:48
 */
@Getter
@AllArgsConstructor
@Schema(description = "弃用状态枚举")
public enum IsDisableEnum {

    @Schema(description = "未弃用")
    NOT_DISABLE(0, "未弃用"),

    @Schema(description = "已弃用")
    DISABLE(1, "已弃用");


    private final Integer value;

    private final String desc;
}
