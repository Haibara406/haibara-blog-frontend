package com.blog.domain.dto;

import com.blog.constants.RegexConstant;
import com.blog.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @author haibara
 * @description 搜索用户列表请求
 * @since 2025/7/28 00:01
 */

@Data
@Schema(description = "搜索用户列表请求")
public class UserSearchDTO {

    @Schema(description = "用户名")
    @Pattern(regexp = RegexConstant.REGEX_USERNAME, message = ValidationConstants.USER_NAME_WRONG_FORMAT)
    @Length(min = 2, max = 32, message = ValidationConstants.USER_NAME_WRONG_FORMAT)
    private String username;

    @Schema(description = "邮箱")
    @Email(message = ValidationConstants.EMAIL_WRONG_FORMAT)
    @Pattern(regexp = RegexConstant.REGEX_EMAIL, message = ValidationConstants.EMAIL_WRONG_MESSAGE)
    @Length(min = 4,message =  ValidationConstants.EMAIL_WRONG_FORMAT)
    private String email;
    //是否禁用 0 否 1 是
    @Schema(description = "是否禁用：0-否，1-是", allowableValues = {"0", "1"})
    private Integer isDisable;

    @Schema(description = "创建时间开始")
    private Date createTimeStart;

    @Schema(description = "创建时间结束")
    private Date createTimeEnd;
}
