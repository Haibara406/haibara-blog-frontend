package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.*;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.UserAccountVO;
import com.blog.domain.vo.UserDetailsVO;
import com.blog.domain.vo.UserListVO;
import com.blog.service.UserService;
import com.blog.utils.ControllerUtils;
import com.blog.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户管理控制器
 *
 * @author haibara
 * @description 用户服务控制器
 * @since 2025/7/27 21:29
 */

@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
@Validated
public class UserController {


    @Resource
    private UserService userService;


    /**
     * 获取当前登录用户的账户信息
     * <p>
     * 查询当前登录用户的详细账户信息，用于前台个人中心页面展示。
     * 返回的信息包含用户基本资料、角色权限、登录状态等完整信息。
     * 该接口需要用户登录后才能访问。
     * 操作完成后会记录操作日志。
     *
     * @return 响应结果，包含当前用户的账户信息
     *         <ul>
     *             <li>成功时返回用户账户信息，包含：
     *                 <ul>
     *                     <li>基本信息：昵称、用户名、头像、邮箱、性别</li>
     *                     <li>账户状态：注册时间、登录状态、是否禁用</li>
     *                     <li>权限信息：用户角色、权限列表</li>
     *                     <li>统计信息：登录次数、最后登录时间等</li>
     *                 </ul>
     *             </li>
     *             <li>失败时返回错误信息，可能是用户未登录或账户异常</li>
     *         </ul>
     * @see UserAccountVO 用户账户信息视图对象
     * @see SecurityUtils#getUserId() 获取当前登录用户ID
     */
    @Operation(summary = "获取当前登录用户信息")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/auth/info")
    public ResponseResult<UserAccountVO> getInfo() {
        return ControllerUtils.messageHandler(() -> userService.findAccountById(SecurityUtils.getUserId()));
    }

    /**
     * 修改当前用户的个人信息
     * <p>
     * 允许登录用户修改自己的个人资料，包括昵称、性别、头像、个人简介等信息。
     * 该接口用于前台个人中心的信息编辑功能。
     * 修改操作会进行数据验证，确保信息的合法性。
     * 操作完成后会记录操作日志。
     *
     * @param userUpdateDTO 用户信息更新数据传输对象，包含：
     *                     <ul>
     *                         <li>nickname: 用户昵称，可选</li>
     *                         <li>gender: 用户性别（0-保密，1-男，2-女），可选</li>
     *                         <li>avatar: 头像URL，可选</li>
     *                         <li>intro: 个人简介，可选</li>
     *                     </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>用户未登录</li>
     *                     <li>数据验证失败</li>
     *                     <li>昵称已被使用</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UserUpdateDTO 用户信息更新数据传输对象
     */
    @Operation(summary = "修改用户信息")
    @Parameter(name = "userUpdateDTO", description = "修改用户信息")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/auth/update")
    public ResponseResult<Void> updateUser(@RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(userUpdateDTO);
    }

    /**
     * 上传用户头像
     * <p>
     * 允许登录用户上传和更换个人头像，支持常见的图片格式。
     * 上传的图片会进行格式验证、大小限制和安全检查。
     * 上传成功后会返回头像的访问URL，用户可以在个人信息中使用。
     * 该接口有特殊的频率限制，防止恶意上传。
     * 操作完成后会记录操作日志。
     *
     * @param avatarFile 头像图片文件，必填参数
     *                  <ul>
     *                      <li>支持的格式：JPG、JPEG、PNG、GIF等</li>
     *                      <li>文件大小限制：通常不超过2MB</li>
     *                      <li>建议尺寸：正方形，至少200x200像素</li>
     *                  </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回头像的访问URL地址</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>文件格式不支持</li>
     *                     <li>文件大小超出限制</li>
     *                     <li>文件上传失败</li>
     *                     <li>用户未登录</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @throws Exception 当文件处理过程中发生异常时抛出
     */
    @Operation(summary = "用户头像上传")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.USER_AVATAR_UPLOAD_MAX_COUNT)
    @PostMapping("/auth/upload/avatar")
    public ResponseResult<String> uploadAvatar(@RequestParam("avatarFile") MultipartFile avatarFile) throws Exception {
        return userService.uploadAvatar(avatarFile);
    }

    /**
     * 修改用户绑定邮箱
     * <p>
     * 允许已登录用户修改绑定的邮箱地址，需要验证新邮箱和当前密码。
     * 该操作用于用户更换邮箱或修复邮箱绑定问题。
     * 修改成功后，用户将使用新邮箱进行登录和接收通知。
     * 操作完成后会记录操作日志。
     *
     * @param updateEmailDTO 邮箱修改数据传输对象，包含：
     *                      <ul>
     *                          <li>newEmail: 新的邮箱地址，必填</li>
     *                          <li>verifyCode: 新邮箱的验证码，必填</li>
     *                          <li>password: 当前账户密码，必填（用于身份验证）</li>
     *                      </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>验证码错误或过期</li>
     *                     <li>密码验证失败</li>
     *                     <li>新邮箱已被其他用户使用</li>
     *                     <li>邮箱格式不正确</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UpdateEmailDTO 邮箱修改数据传输对象
     */
    @Operation(summary = "修改用户绑定邮箱")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/auth/update/email")
    public ResponseResult<Void> updateEmail(@RequestBody @Valid UpdateEmailDTO updateEmailDTO) {
        return userService.updateEmailAndVerify(updateEmailDTO);
    }

    /**
     * 第三方登录用户绑定邮箱
     * <p>
     * 为通过第三方平台（如Gitee、GitHub）登录的用户绑定邮箱地址。
     * 第三方登录用户初次登录时可能没有邮箱信息，通过此接口可以绑定邮箱。
     * 绑定邮箱后，用户可以使用邮箱进行密码登录和接收系统通知。
     * 操作完成后会记录操作日志。
     *
     * @param updateEmailDTO 邮箱绑定数据传输对象，包含：
     *                      <ul>
     *                          <li>email: 要绑定的邮箱地址，必填</li>
     *                          <li>verifyCode: 邮箱验证码，必填</li>
     *                          <li>password: 设置的登录密码，必填（用于后续邮箱登录）</li>
     *                      </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，用户可以使用邮箱登录</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>验证码错误或过期</li>
     *                     <li>邮箱已被其他用户使用</li>
     *                     <li>用户不是第三方登录用户</li>
     *                     <li>邮箱格式不正确</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UpdateEmailDTO 邮箱绑定数据传输对象
     */
    @Operation(summary = "第三方登录用户绑定邮箱")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/auth/third/update/email")
    public ResponseResult<Void> thirdUpdateEmail(@RequestBody @Valid UpdateEmailDTO updateEmailDTO) {
        return userService.thirdUpdateEmail(updateEmailDTO);
    }

    /**
     * 用户注册
     * <p>
     * 处理新用户的注册请求，创建用户账户并完成初始化设置。
     * 注册过程包括用户名唯一性检查、邮箱验证码验证、密码加密存储等步骤。
     * 注册成功后，用户可以使用用户名/邮箱和密码进行登录。
     * 该接口无需登录即可访问，属于公共接口。
     * 操作完成后会记录操作日志。
     *
     * @param userRegisterDTO 用户注册数据传输对象，包含：
     *                       <ul>
     *                           <li>username: 用户名，必填（系统内唯一）</li>
     *                           <li>password: 登录密码，必填（会进行加密存储）</li>
     *                           <li>email: 邮箱地址，必填（系统内唯一）</li>
     *                           <li>verifyCode: 邮箱验证码，必填（用于验证邮箱有效性）</li>
     *                           <li>nickname: 用户昵称，可选（默认使用用户名）</li>
     *                       </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，用户账户创建完成</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>用户名或邮箱已存在</li>
     *                     <li>验证码错误或过期</li>
     *                     <li>密码强度不符合要求</li>
     *                     <li>邮箱格式不正确</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UserRegisterDTO 用户注册数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @Operation(summary = "用户注册")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "前台注册", operation = LogConst.INSERT)
    @PostMapping("/register")
    public ResponseResult<Void> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        return userService.userRegister(userRegisterDTO);
    }

    /**
     * 密码重置确认（第一步）
     * <p>
     * 密码重置流程的第一步，验证用户邮箱和验证码的有效性。
     * 该接口用于确认用户身份，验证通过后用户可以进行下一步的密码重置操作。
     * 这是一个安全验证步骤，确保只有邮箱所有者才能重置密码。
     * 操作完成后会记录操作日志。
     *
     * @param userResetDTO 密码重置确认数据传输对象，包含：
     *                    <ul>
     *                        <li>email: 用户邮箱地址，必填（必须是已注册的邮箱）</li>
     *                        <li>verifyCode: 邮箱验证码，必填（通过公共接口获取）</li>
     *                    </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，用户可以进行密码重置</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>邮箱不存在或未注册</li>
     *                     <li>验证码错误或过期</li>
     *                     <li>邮箱格式不正确</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UserResetConfirmDTO 密码重置确认数据传输对象
     * @see LogConst#RESET_CONFIRM 操作类型：重置确认
     */
    @Operation(summary = "重置密码-确认邮件")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "邮件确认", operation = LogConst.RESET_CONFIRM)
    @PostMapping("/reset-confirm")
    public ResponseResult<Void> resetConfirm(@RequestBody @Valid UserResetConfirmDTO userResetDTO) {
        return userService.userResetConfirm(userResetDTO);
    }

    /**
     * 重置用户密码（第二步）
     * <p>
     * 密码重置流程的第二步，在邮箱验证通过后设置新密码。
     * 该接口用于完成密码重置操作，用户可以使用新密码进行登录。
     * 新密码会进行加密存储，同时会使现有的登录会话失效。
     * 操作完成后会记录操作日志。
     *
     * @param userResetDTO 密码重置数据传输对象，包含：
     *                    <ul>
     *                        <li>email: 用户邮箱地址，必填（必须与确认步骤一致）</li>
     *                        <li>verifyCode: 邮箱验证码，必填（用于二次验证）</li>
     *                        <li>newPassword: 新密码，必填（会进行强度验证和加密存储）</li>
     *                    </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，密码重置完成</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>验证码错误或过期</li>
     *                     <li>邮箱验证失败</li>
     *                     <li>新密码强度不符合要求</li>
     *                     <li>重置流程未完成确认步骤</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UserResetPasswordDTO 密码重置数据传输对象
     * @see LogConst#RESET_PASSWORD 操作类型：重置密码
     */
    @Operation(summary = "重置密码")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "重置密码", operation = LogConst.RESET_PASSWORD)
    @PostMapping("/reset-password")
    public ResponseResult<Void> resetPassword(@RequestBody @Valid UserResetPasswordDTO userResetDTO) {
        return userService.userResetPassword(userResetDTO);
    }

    /**
     * 获取系统用户列表（管理员权限）
     * <p>
     * 查询系统中所有用户的基本信息，用于后台用户管理页面展示。
     * 该接口需要管理员权限，返回的信息包含用户的关键信息但不包含敏感数据。
     * 用户列表按注册时间倒序排列，便于管理员查看最新注册的用户。
     * 操作完成后会记录操作日志。
     *
     * @return 响应结果，包含系统所有用户的列表
     *         <ul>
     *             <li>成功时返回用户列表，每个用户包含：
     *                 <ul>
     *                     <li>基本信息：用户ID、用户名、头像、邮箱</li>
     *                     <li>注册信息：注册方式（邮箱/第三方）、注册时间</li>
     *                     <li>状态信息：是否禁用、最后登录地址</li>
     *                     <li>统计信息：登录次数等</li>
     *                 </ul>
     *             </li>
     *             <li>失败时返回错误信息，可能是权限不足</li>
     *         </ul>
     * @see UserListVO 用户列表视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:user:list')")
    @Operation(summary = "获取用户列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/list")
    public ResponseResult<List<UserListVO>> getUserList() {
        return ControllerUtils.messageHandler(() -> userService.getUserOrSearch(null));
    }

    /**
     * 根据条件搜索用户列表（管理员权限）
     * <p>
     * 根据指定的搜索条件筛选用户记录，支持多种条件组合查询。
     * 该接口用于后台用户管理页面的高级搜索功能，帮助管理员快速定位特定用户。
     * 支持按用户名、邮箱、状态、注册时间等多个维度进行筛选。
     * 操作完成后会记录操作日志。
     *
     * @param userSearchDTO 用户搜索条件数据传输对象，包含：
     *                     <ul>
     *                         <li>username: 用户名关键词搜索，支持模糊匹配</li>
     *                         <li>email: 邮箱关键词搜索，支持模糊匹配</li>
     *                         <li>isDisable: 用户状态筛选（0-正常，1-禁用）</li>
     *                         <li>registerType: 注册方式筛选（0-邮箱，1-Gitee，2-GitHub）</li>
     *                         <li>createTimeStart: 注册时间范围开始</li>
     *                         <li>createTimeEnd: 注册时间范围结束</li>
     *                     </ul>
     * @return 响应结果，包含符合条件的用户列表
     *         <ul>
     *             <li>成功时返回筛选后的用户列表</li>
     *             <li>失败时返回错误信息，可能是权限不足或搜索条件无效</li>
     *         </ul>
     * @see UserSearchDTO 用户搜索条件数据传输对象
     * @see UserListVO 用户列表视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:user:search')")
    @Operation(summary = "搜索用户列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/search")
    public ResponseResult<List<UserListVO>> searchUserList(@RequestBody @Valid UserSearchDTO userSearchDTO) {
        return ControllerUtils.messageHandler(() -> userService.getUserOrSearch(userSearchDTO));
    }

    /**
     * 更新用户状态（管理员权限）
     * <p>
     * 管理员可以通过此接口启用或禁用指定用户账户。
     * 禁用的用户将无法登录系统，已登录的会话也会被强制下线。
     * 该操作通常用于处理违规用户或临时限制用户访问。
     * 操作完成后会记录操作日志。
     *
     * @param updateRoleStatusDTO 用户状态更新数据传输对象，包含：
     *                           <ul>
     *                               <li>id: 要更新状态的用户ID，必填</li>
     *                               <li>status: 新的状态值，必填
     *                                   <ul>
     *                                       <li>0: 正常（启用用户）</li>
     *                                       <li>1: 禁用（禁用用户）</li>
     *                                   </ul>
     *                               </li>
     *                           </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，用户状态更新完成</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>用户不存在</li>
     *                     <li>权限不足</li>
     *                     <li>不能禁用自己的账户</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UpdateRoleStatusDTO 状态更新数据传输对象
     * @see LogConst#UPDATE 操作类型：更新
     */
    @PreAuthorize("hasAnyAuthority('system:user:status:update')")
    @Operation(summary = "更新用户状态")
    @Parameter(name = "roleDeleteDTO", description = "修改用户状态")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "用户管理", operation = LogConst.UPDATE)
    @PostMapping("/update/status")
    public ResponseResult<Void> updateStatus(@RequestBody @Valid UpdateRoleStatusDTO updateRoleStatusDTO) {
        return userService.updateStatus(updateRoleStatusDTO.getId(), updateRoleStatusDTO.getStatus());
    }

    /**
     * 获取用户详细信息（管理员权限）
     * <p>
     * 查询指定用户的完整详细信息，用于后台用户管理的详情查看页面。
     * 返回的信息包含用户的所有非敏感数据，如基本资料、注册信息、登录记录等。
     * 该接口需要管理员权限，用于用户信息审查和问题排查。
     * 操作完成后会记录操作日志。
     *
     * @param id 用户ID，必填参数
     *          <ul>
     *              <li>必须是有效的用户ID</li>
     *              <li>用户必须存在且未被删除</li>
     *          </ul>
     * @return 响应结果，包含用户的详细信息
     *         <ul>
     *             <li>成功时返回用户详细信息，包含：
     *                 <ul>
     *                     <li>基本信息：用户名、昵称、邮箱、头像、性别、个人简介</li>
     *                     <li>账户信息：注册方式、注册时间、状态、角色</li>
     *                     <li>登录信息：最后登录时间、登录地址、登录次数</li>
     *                     <li>统计信息：文章数、评论数等</li>
     *                 </ul>
     *             </li>
     *             <li>失败时返回错误信息，可能是用户不存在或权限不足</li>
     *         </ul>
     * @see UserDetailsVO 用户详细信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:user:details')")
    @Operation(summary = "获取用户详细信息")
    @Parameter(name = "id", description = "用户id")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/details/{id}")
    public ResponseResult<UserDetailsVO> getUserDetails(@PathVariable Long id) {
        return ControllerUtils.messageHandler(() -> userService.findUserDetailsById(id));
    }

    /**
     * 批量删除用户（管理员权限）
     * <p>
     * 管理员可以通过此接口批量删除用户账户，支持同时删除多个用户。
     * 删除操作会彻底移除用户数据，包括用户信息、关联的文章、评论等。
     * 该操作不可逆，请谨慎使用。通常用于清理测试账户或处理严重违规用户。
     * 操作完成后会记录操作日志。
     *
     * @param userDeleteDTO 用户删除数据传输对象，包含：
     *                     <ul>
     *                         <li>ids: 要删除的用户ID列表，必填且不能为空</li>
     *                     </ul>
     *                     注意事项：
     *                     <ul>
     *                         <li>不能删除当前登录的管理员账户</li>
     *                         <li>不能删除超级管理员账户</li>
     *                         <li>删除操作会级联删除相关数据</li>
     *                     </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示删除操作完成</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>用户不存在</li>
     *                     <li>权限不足</li>
     *                     <li>尝试删除受保护的账户</li>
     *                     <li>尝试删除自己的账户</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see UserDeleteDTO 用户删除数据传输对象
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('system:user:delete')")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "用户id")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "用户管理", operation = LogConst.DELETE)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deleteUser(@RequestBody UserDeleteDTO userDeleteDTO) {
        return userService.deleteUser(userDeleteDTO.getIds());
    }
}
