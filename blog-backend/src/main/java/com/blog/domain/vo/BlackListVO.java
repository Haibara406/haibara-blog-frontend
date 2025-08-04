package com.blog.domain.vo;

import com.blog.domain.ip.BlackListIpInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 黑名单列表返回
 * @since 2025/7/28 17:01
 */
@Data
@Schema(description = "黑名单列表VO")
public class BlackListVO {

    @Schema(description = "表id")
    private Long id;

    @Schema(description = "用户名称")
    private String userName;


    @Schema(description = "封禁理由")
    private String reason;

    @Schema(description = "自动封禁、ip信息")
    private BlackListIpInfo ipInfo;

    @Schema(description = "封禁类型")
    private Integer type;

    @Schema(description = "封禁时间")
    private Date bannedTime;

    @Schema(description = "到期时间")
    private Date expiresTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
