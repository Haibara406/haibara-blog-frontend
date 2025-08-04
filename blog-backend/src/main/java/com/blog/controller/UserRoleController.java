package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.ErrorConst;
import com.blog.constants.LogConst;
import com.blog.constants.ValidationConstants;
import com.blog.domain.dto.RoleUserDTO;
import com.blog.domain.vo.RoleUserVO;
import com.blog.domain.dto.UserRoleDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.RoleAllVO;
import com.blog.enums.SelRoleUserEnum;
import com.blog.enums.SelUserRoleEnum;
import com.blog.service.UserRoleService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色关系管理控制器
 *
 * @author haibara
 * @description 用户角色相关接口
 * @since 2025/7/29 14:45
 */

@RestController
@Tag(name = "用户角色相关接口")
@RequestMapping("user/role")
@Validated
public class UserRoleController {

    @Resource
    private UserRoleService userRoleService;

    /**
     * 查询拥有指定角色的用户列表
     * <p>
     * 根据角色ID查询所有拥有该角色的用户信息，支持按用户名和邮箱进行过滤。
     * 该接口用于角色管理页面显示已授权的用户列表。
     *
     * @param roleId 角色ID，必填参数，用于指定要查询的角色
     * @param username 用户名，可选参数，用于模糊匹配用户名进行过滤
     * @param email 邮箱，可选参数，用于模糊匹配邮箱进行过滤
     * @return 响应结果，包含拥有指定角色的用户列表
     *         <ul>
     *             <li>成功时返回用户列表，每个用户包含ID、昵称、用户名、邮箱、禁用状态和创建时间</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当角色ID为空时抛出
     * @see RoleUserVO 用户角色信息视图对象
     * @see SelRoleUserEnum#HAS_ROLE 查询类型：拥有角色
     */
    @PreAuthorize("hasAnyAuthority('system:user:role:list')")
    @Operation(summary = "查询拥有角色的用户列表")
    @Parameters({
            @Parameter(name = "roleId", description = "角色id"),
            @Parameter(name = "username", description = "用户名"),
            @Parameter(name = "email", description = "邮箱")
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/user/list")
    public ResponseResult<List<RoleUserVO>> selectUser(
            @NotNull(message = ValidationConstants.ROLE_ID_NOT_NULL) Long roleId,
            @RequestParam(required = false,name = "username") String username,
            @RequestParam(required = false,name = "email") String email
    ) {
        return ControllerUtils.messageHandler(() -> userRoleService
                .selectRoleUser(roleId,username,email, SelRoleUserEnum.HAS_ROLE.getType()));
    }


    /**
     * 查询没有指定角色的用户列表
     * <p>
     * 根据角色ID查询所有没有该角色的用户信息，支持按用户名和邮箱进行过滤。
     * 该接口用于角色管理页面显示可授权的用户列表，便于管理员为角色分配用户。
     *
     * @param roleId 角色ID，必填参数，用于指定要查询的角色
     * @param username 用户名，可选参数，用于模糊匹配用户名进行过滤
     * @param email 邮箱，可选参数，用于模糊匹配邮箱进行过滤
     * @return 响应结果，包含没有指定角色的用户列表
     *         <ul>
     *             <li>成功时返回用户列表，每个用户包含ID、昵称、用户名、邮箱、禁用状态和创建时间</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当角色ID为空时抛出
     * @see RoleUserVO 用户角色信息视图对象
     * @see SelRoleUserEnum#NO_ROLE 查询类型：没有角色
     */
    @PreAuthorize("hasAnyAuthority('system:not:role:user:list')")
    @Operation(summary = "查询未拥有角色的用户列表")
    @Parameters({
            @Parameter(name = "roleId", description = "角色id"),
            @Parameter(name = "username", description = "用户名"),
            @Parameter(name = "email", description = "邮箱")
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/not/user/list")
    public ResponseResult<List<RoleUserVO>> selectNotUserByRole(
            @NotNull(message = ValidationConstants.ROLE_ID_NOT_NULL) Long roleId,
            @RequestParam(required = false,name = "username") String username,
            @RequestParam(required = false,name = "email") String email
    ) {
        return ControllerUtils.messageHandler(() -> userRoleService
                .selectRoleUser(roleId,username,email,SelRoleUserEnum.NO_ROLE.getType()));
    }


    /**
     * 添加用户角色关系
     * <p>
     * 为指定的角色批量授予用户，支持一个角色同时授予多个用户。
     * 该操作会先删除已存在的相同用户角色关系，然后重新创建，确保数据的一致性。
     * 操作完成后会记录操作日志。
     *
     * @param userRoleDTO 用户角色关系数据传输对象，包含：
     *                   <ul>
     *                       <li>roleId: 角色ID，不能为空</li>
     *                       <li>userId: 用户ID列表，不能为空</li>
     *                   </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当角色ID或用户ID列表为空时抛出
     * @see UserRoleDTO 用户角色关系数据传输对象
     * @see LogConst#GRANT 操作类型：授权
     */
    @PreAuthorize("hasAnyAuthority('system:user:role:add')")
    @Operation(summary = "添加用户角色关系")
    @Parameter(name = "userRoleDTO", description = "添加的数据")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="用户角色",operation= LogConst.GRANT)
    @PostMapping("/add")
    public ResponseResult<Void> addUserRole(@Valid @RequestBody UserRoleDTO userRoleDTO) {
        return userRoleService.addUserRole(userRoleDTO);
    }


    /**
     * 删除用户角色关系
     * <p>
     * 批量取消指定角色的用户，支持同时取消一个角色的多个用户。
     * 该操作会删除用户角色关系表中对应的记录，操作完成后会记录操作日志。
     *
     * @param userRoleDTO 用户角色关系数据传输对象，包含：
     *                   <ul>
     *                       <li>roleId: 要取消的角色ID，不能为空</li>
     *                       <li>userId: 要取消角色的用户ID列表，不能为空</li>
     *                   </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示角色已成功取消</li>
     *             <li>失败时返回错误信息，可能是因为角色关系不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当角色ID或用户ID列表为空时抛出
     * @see UserRoleDTO 用户角色关系数据传输对象
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('system:user:role:delete')")
    @Operation(summary = "删除用户角色关系")
    @Parameter(name = "userRoleDTO", description = "删除的所需数据")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="用户角色",operation= LogConst.DELETE)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deleteUserRole(@Valid @RequestBody UserRoleDTO userRoleDTO) {
        return userRoleService.deleteUserRole(userRoleDTO);
    }


    /**
     * 查询拥有指定用户的角色列表
     * <p>
     * 根据用户ID查询所有拥有该用户的角色信息，支持按角色名称和角色标识进行过滤。
     * 该接口用于用户管理页面显示已授权的角色列表。
     *
     * @param userId 用户ID，必填参数，用于指定要查询的用户
     * @param roleName 角色名称，可选参数，用于模糊匹配角色名称进行过滤
     * @param roleKey 角色标识，可选参数，用于模糊匹配角色标识进行过滤
     * @return 响应结果，包含拥有指定用户的角色列表
     *         <ul>
     *             <li>成功时返回角色列表，每个角色包含ID、名称、标识、状态、排序号和创建时间</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当用户ID为空时抛出
     * @see RoleAllVO 角色信息视图对象
     * @see SelUserRoleEnum#HAS_USER 查询类型：拥有用户
     */
    @PreAuthorize("hasAnyAuthority('system:role:user:list')")
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "roleName", description = "角色名称"),
            @Parameter(name = "roleKey", description = "角色键")
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Operation(summary = "查询拥有用户的角色列表")
    @GetMapping("/role/list")
    public ResponseResult<List<RoleAllVO>> selectPermissionIdRole(
            @NotNull(message = ValidationConstants.USER_ID_NOT_NULL) Long userId,
            @RequestParam(required = false, name = "roleName") String roleName,
            @RequestParam(required = false, name = "roleKey") String roleKey
    ) {
        return ControllerUtils.messageHandler(() -> userRoleService
                .selectRoleByUserId(userId,roleName,roleKey, SelUserRoleEnum.HAS_USER.getType()));
    }


    /**
     * 查询没有指定用户的角色列表
     * <p>
     * 根据用户ID查询所有没有该用户的角色信息，支持按角色名称和角色标识进行过滤。
     * 该接口用于用户管理页面显示可授权的角色列表，便于管理员为用户分配角色。
     *
     * @param userId 用户ID，必填参数，用于指定要查询的用户
     * @param roleName 角色名称，可选参数，用于模糊匹配角色名称进行过滤
     * @param roleKey 角色标识，可选参数，用于模糊匹配角色标识进行过滤
     * @return 响应结果，包含没有指定用户的角色列表
     *         <ul>
     *             <li>成功时返回角色列表，每个角色包含ID、名称、标识、状态、排序号和创建时间</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当用户ID为空时抛出
     * @see RoleAllVO 角色信息视图对象
     * @see SelUserRoleEnum#NO_USER 查询类型：没有用户
     */
    @PreAuthorize("hasAnyAuthority('system:user:role:not:list')")
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "roleName", description = "角色名称"),
            @Parameter(name = "roleKey", description = "角色键")
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Operation(summary = "查询没有该用户的角色列表")
    @GetMapping("/not/role/list")
    public ResponseResult<List<RoleAllVO>> selectUserNotRole(
            @NotNull(message = ValidationConstants.USER_ID_NOT_NULL) Long userId,
            @RequestParam(required = false, name = "roleName") String roleName,
            @RequestParam(required = false, name = "roleKey") String roleKey
    ) {
        return ControllerUtils.messageHandler(() -> userRoleService
                .selectRoleByUserId(userId,roleName,roleKey,SelUserRoleEnum.NO_USER.getType()));
    }


    /**
     * 添加角色用户关系
     * <p>
     * 为指定的用户批量授予角色，支持多个用户同时授予多个角色。
     * 该操作会先删除已存在的相同角色用户关系，然后重新创建，确保数据的一致性。
     * 操作完成后会记录操作日志。
     *
     * @param roleUserDTO 角色用户关系数据传输对象，包含：
     *                   <ul>
     *                       <li>userId: 用户ID列表，不能为空</li>
     *                       <li>roleId: 角色ID列表，不能为空</li>
     *                   </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当用户ID列表或角色ID列表为空时抛出
     * @see RoleUserDTO 角色用户关系数据传输对象
     * @see LogConst#GRANT 操作类型：授权
     */
    @Operation(summary = "添加角色用户关系")
    @PreAuthorize("hasAnyAuthority('system:user:role:add')")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Parameters({
            @Parameter(name = "roleUserDTO", description = "添加的数据")
    })
    @LogAnnotation(module="角色用户",operation= LogConst.GRANT)
    @PostMapping("/user/add")
    public ResponseResult<Void> addRoleUser(@Valid @RequestBody RoleUserDTO roleUserDTO) {
        return userRoleService.addRoleUser(roleUserDTO);
    }


    /**
     * 删除角色用户关系
     * <p>
     * 批量取消指定用户的角色，支持同时取消多个用户的多个角色。
     * 该操作会删除用户角色关系表中对应的记录，操作完成后会记录操作日志。
     *
     * @param roleUserDTO 角色用户关系数据传输对象，包含：
     *                   <ul>
     *                       <li>userId: 要取消角色的用户ID列表，不能为空</li>
     *                       <li>roleId: 要取消的角色ID列表，不能为空</li>
     *                   </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示角色已成功取消</li>
     *             <li>失败时返回错误信息，可能是因为角色关系不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当用户ID列表或角色ID列表为空时抛出
     * @see RoleUserDTO 角色用户关系数据传输对象
     * @see LogConst#DELETE 操作类型：删除
     */
    @Operation(summary = "删除角色用户关系")
    @PreAuthorize("hasAnyAuthority('system:user:role:delete')")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Parameters({
            @Parameter(name = "roleUserDTO", description = "删除的所需数据")
    })
    @LogAnnotation(module="角色用户",operation= LogConst.DELETE)
    @DeleteMapping("/user/delete")
    public ResponseResult<Void> deleteRoleUser(@Valid @RequestBody RoleUserDTO roleUserDTO) {
        return userRoleService.deleteRoleUser(roleUserDTO);
    }
}
