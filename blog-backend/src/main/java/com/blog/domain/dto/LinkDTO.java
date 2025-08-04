package com.blog.domain.dto;

import com.blog.constants.RegexConstant;
import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author haibara
 * @description 申请友链请求
 * @since 2025/8/1 12:00
 */
@Data
@Schema(description = "申请友链请求")
public class LinkDTO implements BaseData {

    @Schema(description = "网站名称")
    @Length(max = 15, message = ValidationConstants.WEBSITE_NAME_TOO_LONG)
    private String name;

    @Schema(description = "网站地址")
    @Length(max = 50, message = ValidationConstants.WEBSITE_URL_TOO_LONG)
    private String url;

    @Schema(description = "网站描述")
    @Length(max = 30, message = ValidationConstants.WEBSITE_DESCRIPTION_TOO_LONG)
    private String description;

    @Schema(description = "网站背景")
    @Length(max = 200, message = ValidationConstants.WEBSITE_BACKGROUND_TOO_LONG)
    private String background;

    @Schema(description = "邮箱地址")
    @Email(message = ValidationConstants.EMAIL_WRONG_FORMAT)
    @Pattern(regexp = RegexConstant.REGEX_EMAIL, message = ValidationConstants.EMAIL_WRONG_MESSAGE)
    @Length(min = 4, message =  ValidationConstants.EMAIL_WRONG_FORMAT)
    private String email;
}
