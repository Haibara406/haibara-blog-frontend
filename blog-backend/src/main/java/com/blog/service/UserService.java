package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.constants.ValidationConstants;
import com.blog.domain.dto.*;
import com.blog.domain.entity.User;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.UserAccountVO;
import com.blog.domain.vo.UserDetailsVO;
import com.blog.domain.vo.UserListVO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author haibara
 * @description 用户服务接口
 * @since 2025/7/27
 */
public interface UserService extends IService<User> , UserDetailsService {

    /**
     * 更新用户登录状态
     *
     * @param id 用户ID
     * @param type 登录类型
     */
    void userLoginStatus(Long id, Integer type);

    /**
     * 根据用户ID查找用户账户信息
     *
     * @param userId 用户ID
     * @return 用户账户视图对象
     */
    UserAccountVO findAccountById(Long userId);

    /**
     * 更新用户信息
     *
     * @param userUpdateDTO 用户更新数据传输对象
     * @return 操作结果
     */
    ResponseResult<Void> updateUser(UserUpdateDTO userUpdateDTO);

    /**
     * 上传用户头像
     *
     * @param avatarFile 头像文件
     * @return 上传结果，包含头像URL
     * @throws Exception 上传异常
     */
    ResponseResult<String> uploadAvatar(MultipartFile avatarFile) throws Exception;

    /**
     * 更新邮箱并验证
     *
     * @param updateEmailDTO 更新邮箱数据传输对象
     * @return 操作结果
     */
    ResponseResult<Void> updateEmailAndVerify(UpdateEmailDTO updateEmailDTO);

    /**
     * 第三方用户更新邮箱
     *
     * @param updateEmailDTO 更新邮箱数据传输对象
     * @return 操作结果
     */
    ResponseResult<Void> thirdUpdateEmail(UpdateEmailDTO updateEmailDTO);

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册数据传输对象
     * @return 注册结果
     */
    ResponseResult<Void> userRegister(UserRegisterDTO userRegisterDTO);

    /**
     * 用户重置密码确认
     *
     * @param userResetDTO 用户重置确认数据传输对象
     * @return 确认结果
     */
    ResponseResult<Void> userResetConfirm(UserResetConfirmDTO userResetDTO);

    /**
     * 用户重置密码
     *
     * @param userResetDTO 用户重置密码数据传输对象
     * @return 重置结果
     */
    ResponseResult<Void> userResetPassword(UserResetPasswordDTO userResetDTO);

    /**
     * 获取用户列表或搜索用户
     *
     * @param userSearchDTO 用户搜索条件
     * @return 用户列表
     */
    List<UserListVO> getUserOrSearch(UserSearchDTO userSearchDTO);

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 用户状态（0-启用，1-禁用）
     * @return 更新结果
     */
    ResponseResult<Void> updateStatus(@NotNull(message = ValidationConstants.USER_ID_NOT_NULL) Long id,
                                      @Min(value = 0, message =  ValidationConstants.ROLE_STATUS_VALUE_WRONG)
                                      @Max(value = 1, message =  ValidationConstants.ROLE_STATUS_VALUE_WRONG) Integer status);

    /**
     * 根据ID查找用户详细信息
     *
     * @param id 用户ID
     * @return 用户详细信息视图对象
     */
    UserDetailsVO findUserDetailsById(Long id);

    /**
     * 删除用户
     *
     * @param ids 用户ID列表
     * @return 删除结果
     */
    ResponseResult<Void> deleteUser(@NotNull List<Long> ids);
}
