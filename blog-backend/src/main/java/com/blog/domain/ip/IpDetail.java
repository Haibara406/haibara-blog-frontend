package com.blog.domain.ip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haibara
 * @description ip详情
 * @since 2025/7/27 12:22
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "IP详情信息")
public class IpDetail {

    @Schema(description = "IP地址", example = "192.168.1.1")
    private String ip;

    @Schema(description = "网络服务提供商", example = "中国电信")
    private String isp;

    @Schema(description = "网络服务提供商ID", example = "100017")
    private String isp_id;

    @Schema(description = "城市", example = "北京")
    private String city;

    @Schema(description = "城市ID", example = "131")
    private String city_id;

    @Schema(description = "国家", example = "中国")
    private String country;

    @Schema(description = "国家ID", example = "CN")
    private String country_id;

    @Schema(description = "地区", example = "华北")
    private String region;

    @Schema(description = "地区ID", example = "100000")
    private String region_id;
}
