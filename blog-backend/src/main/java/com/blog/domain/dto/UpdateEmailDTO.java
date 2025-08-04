package com.blog.domain.dto;

import com.blog.constants.RegexConstant;
import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author haibara
 * @description 邮箱更新请求
 * @since 2025/7/27 21:48
 */
@Data
@Schema(description = "邮箱更新请求")
public class UpdateEmailDTO {

    @Schema(description = "验证码")
    @NotEmpty(message = ValidationConstants.CODE_NOT_NULL)
    private String code;

    @Schema(description = "邮箱")
    @Email(message = ValidationConstants.EMAIL_WRONG_FORMAT)
    @Pattern(regexp = RegexConstant.REGEX_EMAIL, message = ValidationConstants.EMAIL_WRONG_MESSAGE)
    @Length(min = 4, message =  ValidationConstants.EMAIL_WRONG_FORMAT)
    private String email;

    @Schema(description = "密码")
    @NotEmpty(message = ValidationConstants.PASSWORD_NOT_NULL)
    private String password;
}