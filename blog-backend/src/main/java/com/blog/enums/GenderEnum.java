package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 性别枚举
 * @since 2025/7/28 01:06
 */

@Getter
@AllArgsConstructor
@Schema(description = "性别枚举")
public enum GenderEnum {

    @Schema(description = "未定义")
    UNDEFINED(0, "未定义"),

    @Schema(description = "男")
    MALE(1, "男"),

    @Schema(description = "女")
    FEMALE(2, "女");

    // 类型
    private final Integer value;
    // 描述
    private final String desc;
}
