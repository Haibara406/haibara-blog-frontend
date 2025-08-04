package com.blog.domain.request.oauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haibara
 * @description OAuth-github登录
 * @since 2025/7/27 12:09
 */
@Data
@Component
@ConfigurationProperties(value = "oauth.github")
@Schema(description = "GitHub OAuth配置")
public class GithubBody {

    @Schema(description = "GitHub客户端ID", example = "your_client_id")
    private String clientId;

    @Schema(description = "GitHub登录后的回调地址", example = "https://example.com/oauth/github/callback")
    private String redirectUri;

    @Schema(description = "GitHub客户端密钥", hidden = true)
    private String clientSecret;
}

