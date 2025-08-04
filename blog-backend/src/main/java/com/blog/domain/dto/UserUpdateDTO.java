package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author haibara
 * @description 用户信息更新请求
 * @since 2025/7/27 21:45
 */
@Data
@Schema(description = "用户信息更新请求")
public class UserUpdateDTO implements BaseData {

    @NotNull(message = ValidationConstants.USER_NICKNAME_NOT_NULL)
    @Schema(description = "用户昵称")
    private String nickname;

    @NotNull(message = ValidationConstants.USER_GENDER_NOT_NULL)
    @Schema(description = "用户性别：0-未定义，1-男，2-女",allowableValues = {"0", "1", "2"})
    private Integer gender;

    @NotNull(message = ValidationConstants.USER_AVATAR_NOT_NULL)
    @Schema(description = "用户头像")
    private String avatar;

    @NotNull(message = ValidationConstants.USER_INTRO_NOT_NULL)
    @Schema(description = "个人简介")
    private String intro;
}
