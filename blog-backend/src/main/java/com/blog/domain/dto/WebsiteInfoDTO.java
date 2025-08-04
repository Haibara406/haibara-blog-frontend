package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @author haibara
 * @description 网站信息操作请求
 * @since 2025/7/29 16:25
 */
@Data
@Schema(description = "网站信息操作请求")
public class WebsiteInfoDTO implements BaseData {

    @Length(max = 30, message = ValidationConstants.WEBSITE_NAME_IS_TOO_LONG)
    @Schema(description = "网站名称")
    private String websiteName;

    @Length(max = 100, message = ValidationConstants.HEADER_NOTIFICATION_IS_TOO_LONG)
    @Schema(description = "头部通知")
    private String headerNotification;

    @Length(max = 1000, message = ValidationConstants.SIDEBAR_ANNOUNCEMENT_IS_TOO_LONG)
    @Schema(description = "侧面公告")
    private String sidebarAnnouncement;

    @Length(max = 100, message = ValidationConstants.RECORD_INFO_IS_TOO_LONG)
    @Schema(description = "备案信息")
    private String recordInfo;

    @Schema(description = "开始运行时间")
    private Date startTime;
}
