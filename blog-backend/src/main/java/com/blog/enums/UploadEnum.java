package com.blog.enums;

import com.blog.constants.ImageConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author haibara
 * @description 文件上传枚举
 * @since 2025/7/27 15:47
 */

@Getter
@AllArgsConstructor
@Schema(description = "文件上传类型枚举")
public enum UploadEnum {

    // 站长头像
    @Schema(description = "站长头像")
    WEBSITE_INFO_AVATAR("websiteInfo/avatar/", "站长头像", List.of(ImageConst.JPG, ImageConst.JPEG, ImageConst.PNG, ImageConst.WEBP), 5.0, 0),
    // 站长背景
    @Schema(description = "站长背景")
    WEBSITE_INFO_BACKGROUND("websiteInfo/background/", "站长背景", List.of(ImageConst.JPG, ImageConst.JPEG, ImageConst.PNG, ImageConst.WEBP), 5.0, 1),
    // 文章封面
    @Schema(description = "文章封面")
    ARTICLE_COVER("article/articleCover/", "文章封面", List.of(ImageConst.JPG, ImageConst.JPEG, ImageConst.PNG, ImageConst.WEBP), 5.0, 2),
    // 文章图片
    @Schema(description = "文章图片")
    ARTICLE_IMAGE("article/articleImage/", "文章图片", List.of(ImageConst.JPG, ImageConst.JPEG, ImageConst.PNG, ImageConst.GIF, ImageConst.WEBP), 5.0, 3),
    // 用户头像
    @Schema(description = "用户头像")
    USER_AVATAR("user/avatar/", "用户头像", List.of(ImageConst.JPG, ImageConst.JPEG, ImageConst.PNG, ImageConst.WEBP), 5.0, 4),
    // 前台首页Banners图片
    @Schema(description = "前台首页Banners图片")
    UI_BANNERS("banners/", "前台首页Banners图片", List.of(ImageConst.JPG, ImageConst.JPEG, ImageConst.PNG, ImageConst.WEBP), 8.0, 5),
    // 相册模块图片
    @Schema(description = "相册模块图片")
    PHOTO_ALBUM("photoAlbum/", "相册模块图片", List.of(ImageConst.JPG, ImageConst.JPEG, ImageConst.PNG, ImageConst.WEBP, ImageConst.GIF), 8.0, 6);


    // 上传目录
    private final String dir;

    // 描述
    private final String description;

    // 支持的格式
    private final List<String> format;

    // 文件最大大小 单位：MB
    private final Double limitSize;

    // 上传的类型
    private final int type;
}
