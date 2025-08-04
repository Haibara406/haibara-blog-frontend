package com.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.blog.domain.BaseData;

import java.util.Date;


/**
 * @author haibara
 * @description sys_website_info表实体类
 * @since 2025/7/27 14:20
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@TableName("sys_website_info")
@Schema(description = "网站信息实体")
public class WebsiteInfo implements BaseData {

    @Schema(description = "网站信息ID", example = "1")
    private Long id;

    @Schema(description = "站长头像URL", example = "https://example.com/avatar.jpg")
    private String webmasterAvatar;

    @Schema(description = "站长名称", example = "Haibara")
    private String webmasterName;

    @Schema(description = "站长文案", example = "不经一番寒彻骨，怎得梅花扑鼻香")
    private String webmasterCopy;

    @Schema(description = "站长资料卡背景图URL", example = "https://example.com/background.jpg")
    private String webmasterProfileBackground;

    @Schema(description = "Gitee链接", example = "https://gitee.com/username")
    private String giteeLink;

    @Schema(description = "GitHub链接", example = "https://github.com/username")
    private String githubLink;

    @Schema(description = "网站名称", example = "Haiabra-Blog")
    private String websiteName;

    @Schema(description = "头部通知", example = "欢迎访问我的个人博客，希望你能喜欢！")
    private String headerNotification;

    @Schema(description = "侧面公告", example = "欢迎指出网站的不足，给我提供意见")
    private String sidebarAnnouncement;

    @Schema(description = "备案信息", example = "备案信息")
    private String recordInfo;

    @Schema(description = "网站开始运行时间", example = "2024-01-01 16:00:25")
    private Date startTime;

    @Schema(description = "创建时间", example = "2025-07-27 14:30:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间", example = "2025-07-27 15:30:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0", allowableValues = {"0", "1"})
    private Integer isDeleted;
}

