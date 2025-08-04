package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 黑名单枚举
 * @since 2025/7/27 15:50
 */

@Getter
@AllArgsConstructor
@Schema(description = "黑名单状态枚举")
public enum BlackListEnum {


    @Schema(description = "封禁")
    IS_BANNED(0, "封禁"),

    @Schema(description = "未封禁")
    IS_NOT_BANNED(1, "未封禁");

    private final Integer code;
    private final String desc;
}
