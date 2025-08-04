package com.blog.domain.dto;

import com.blog.constants.RegexConstant;
import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author haibara
 * @description 用户重置密码请求
 * @since 2025/7/27 22:18
 */

@Data
@Schema(description = "用户重置密码请求")
public class UserResetPasswordDTO {

    @Schema(description = "密码")
    @Length(min = 8, max = 24, message = ValidationConstants.PASSWORD_NOT_STRONG)
    @Pattern(regexp = RegexConstant.REGEX_PASSWORD_STRONG, message = ValidationConstants.PASSWORD_NOT_STRONG)
    private String password;

    @Schema(description = "验证码")
    @Length(max = 6, min = 6)
    private String code;

    @Schema(description = "邮箱")
    @Email(message = ValidationConstants.EMAIL_WRONG_FORMAT)
    @Pattern(regexp = RegexConstant.REGEX_EMAIL, message = ValidationConstants.EMAIL_WRONG_MESSAGE)
    @Length(min = 4, message = ValidationConstants.EMAIL_WRONG_FORMAT)
    private String email;

}
