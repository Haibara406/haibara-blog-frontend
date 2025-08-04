package com.blog.domain.ip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haibara
 * @description 黑名单ip
 * @since 2025/7/27 12:21
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "黑名单IP信息")
public class BlackListIpInfo {

    @Schema(description = "黑名单IP地址", example = "192.168.1.100")
    private String createIp;

    @Schema(description = "IP详情信息")
    private IpDetail ipDetail;
}
