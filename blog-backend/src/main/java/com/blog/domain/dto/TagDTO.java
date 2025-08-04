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
 * @description 标签相关操作请求
 * @since 2025/7/30 21:32
 */
@Accessors(chain = true)
@Data
@Schema(description = "标签相关操作请求")
public class TagDTO implements BaseData {

    @Schema(description = "标签id")
    private Long id;

    @Schema(description = "标签名称")
    @NotBlank(message = ValidationConstants.TAG_NAME_NOT_NULL)
    @Length(max = 20, message = ValidationConstants.TAG_NAME_IS_TOO_LONG)
    private String tagName;
}
