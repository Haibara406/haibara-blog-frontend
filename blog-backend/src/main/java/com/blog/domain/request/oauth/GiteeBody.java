package com.blog.domain.request.oauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haibara
 * @description OAuth-gitee登录
 * @since 2025/7/27 12:08
 */
@Data
@Component
@ConfigurationProperties(value = "oauth.gitee")
@Schema(description = "Gitee OAuth配置")
public class GiteeBody {

    @Schema(description = "Gitee客户端ID", example = "your_client_id")
    private String clientId;

    @Schema(description = "Gitee登录后的回调地址", example = "https://example.com/oauth/gitee/callback")
    private String redirectUri;

    @Schema(description = "Gitee客户端密钥", hidden = true)
    private String clientSecret;
}
