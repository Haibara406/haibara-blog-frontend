package com.blog.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author haibara
 * @description 相册枚举
 * @since 2025/7/27 15:50
 */

@Getter
@AllArgsConstructor
@Schema(description = "相册或照片类型枚举")
public enum AlbumOrPhotoEnum {

    @Schema(description = "相册")
    ALBUM(1, "相册"),

    @Schema(description = "照片")
    PHOTO(2, "照片");

    private final Integer code;
    private final String desc;
}
