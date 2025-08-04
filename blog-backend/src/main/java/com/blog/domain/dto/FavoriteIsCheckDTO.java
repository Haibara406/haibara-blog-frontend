package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 修改收藏是否通过请求
 * @since 2025/7/31 21:18
 */
@Data
@Schema(description = "修改收藏是否通过请求")
public class FavoriteIsCheckDTO {

    @NotNull(message = ValidationConstants.FAVORITE_ID_NOT_NULL)
    @Schema(description = "收藏id")
    private Long id;


    @NotNull(message = ValidationConstants.FAVORITE_IS_CHECK_NOT_NULL)
    @Schema(description = "是否有效")
    private Integer isCheck;
}