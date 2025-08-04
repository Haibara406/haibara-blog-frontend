package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 修改树洞是否通过请求
 * @since 2025/7/31 11:57
 */
@Data
@Schema(description = "修改树洞是否通过请求")
public class TreeHoleIsCheckDTO {

    @Schema(description = "树洞id")
    @NotNull(message = ValidationConstants.TREE_HOLE_ID_NOT_NULL)
    private Long id;

    @Schema(description = "是否通过")
    @NotNull(message = ValidationConstants.TREE_HOLE_IS_CHECK_NOT_NULL)
    private Integer isCheck;
}
