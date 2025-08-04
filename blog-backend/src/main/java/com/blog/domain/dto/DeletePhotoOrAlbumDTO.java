package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 删除照片或相册请求
 * @since 2025/8/1 22:57
 */
@Data
@Schema(description = "删除照片或相册请求")
public class DeletePhotoOrAlbumDTO {

    @NotNull(message = ValidationConstants.ALBUM_OR_PHOTO_ID_NOT_NULL)
    @Schema(description = "照片或相册id")
    private Long id;

    @Min(value = 1, message = ValidationConstants.TYPE_ERROR)
    @Max(value = 2, message = ValidationConstants.TYPE_ERROR)
    @Schema(description = "类型：1-相册，2-照片")
    private Integer type;

    @Schema(description = "父相册id")
    private Long parentId;
}