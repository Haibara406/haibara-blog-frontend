package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 授权返回
 * @since 2025/7/28 18:15
 */
@Data
@Schema(name = "AuthorizeVO", description = "授权VO")
public class AuthorizeVO {

    @Schema(description = "token")
    private String token;

    @Schema(description = "token 过期时间")
    private Date expire;
}
