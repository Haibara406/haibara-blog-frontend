package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 请求头枚举
 * @since 2025/7/27 15:55
 */
@Getter
@AllArgsConstructor
@Schema(description = "请求头枚举")
public enum RequestHeaderEnum {

    /**
     * Github获取个人信息Accept请求头
     */
    @Schema(description = "Github获取个人信息Accept请求头")
    GITHUB_USER_INFO("Accept", "application/vnd.github.v3+json");


    /**
     * 请求头
     */
    public final String header;

    /**
     * 内容
     */
    public final String content;
}
