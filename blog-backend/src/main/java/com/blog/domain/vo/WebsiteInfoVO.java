package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 网站信息返回
 * @since 2025/7/29 16:18
 */
@Data
@Schema(description = "网站信息返回")
public class WebsiteInfoVO {

    @Schema(description = "站长头像URL")
    private String webmasterAvatar;

    @Schema(description = "站长名称")
    private String webmasterName;

    @Schema(description = "站长文案")
    private String webmasterCopy;

    @Schema(description = "站长资料卡背景图URL")
    private String webmasterProfileBackground;

    @Schema(description = "Gitee链接")
    private String giteeLink;

    @Schema(description = "GitHub链接")
    private String githubLink;

    @Schema(description = "网站名称")
    private String websiteName;

    @Schema(description = "头部通知")
    private String headerNotification;

    @Schema(description = "侧面公告")
    private String sidebarAnnouncement;

    @Schema(description = "备案信息")
    private String recordInfo;

    @Schema(description = "开始运行时间")
    private Date startTime;

    @Schema(description = "文章的最后更新时间")
    private Date lastUpdateTime;

    @Schema(description = "文章数目")
    private Long articleCount;

    @Schema(description = "分类数")
    private Long categoryCount;

    @Schema(description = "评论数")
    private Long commentCount;

    @Schema(description = "全站字数")
    private Long wordCount;

    @Schema(description = "访问次数")
    private Long visitCount;

}
