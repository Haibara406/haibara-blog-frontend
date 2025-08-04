package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 状态枚举
 * @since 2025/7/28 15:31
 */

@Getter
@AllArgsConstructor
@Schema(description = "操作状态枚举")
public enum StateEnum {

    @Schema(description = "成功")
    STATE_NORMAL(0,"成功"),

    @Schema(description = "失败")
    STATE_FAILED(1,"失败");

    // 类型
    private final Integer status;
    // 描述
    private final String desc;
}
