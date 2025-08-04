package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author haibara
 * @description 创建或修改相册请求
 * @since 2025/8/1 22:54
 */
@Data
@Schema(description = "创建或修改相册请求")
public class PhotoAlbumDTO {

    @Schema(description = "相册id")
    private Long id;

    @Schema(description = "父相册id")
    private Long parentId;

    @NotEmpty(message = ValidationConstants.ALBUM_NAME_NOT_NULL)
    @Size(max = 20, message = ValidationConstants.ALBUM_NAME_IS_TOO_LONG)
    @Schema(description = "相册名称")
    private String name;

    @Schema(description = "相册描述")
    private String description;
}
