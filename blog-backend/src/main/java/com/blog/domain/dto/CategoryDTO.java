package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * @author haibara
 * @description 分类相关操作请求
 * @since 2025/7/30 19:24
 */
@Accessors(chain = true)
@Data
@Schema(description = "分类相关操作请求")
public class CategoryDTO implements BaseData {

    @Schema(description = "分类id")
    private Long id;

    @NotBlank(message = ValidationConstants.GATEGORY_NAME_NOT_NULL)
    @Length(max = 20, message = ValidationConstants.GATEGORY_NAME_IS_TOO_LONG)
    @Schema(description = "分类名称")
    private String categoryName;
}
