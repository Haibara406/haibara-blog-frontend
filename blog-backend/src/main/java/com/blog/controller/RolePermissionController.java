package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.RolePermissionDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.RoleAllVO;
import com.blog.enums.SelPermTypeEnum;
import com.blog.service.RolePermissionService;
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
 * 角色权限关系管理控制器
 *
 * @author haibara
 * @description 角色权限相关接口
 * @since 2025/7/29 11:37
 */

@RestController
@Tag(name = "用户角色相关接口")
@RequestMapping("role/permission")
@Validated
public class RolePermissionController {


    @Resource
    private RolePermissionService rolePermissionService;
    

    /**
     * 查询拥有指定权限的角色列表
     * <p>
     * 根据权限ID查询所有拥有该权限的角色信息，支持按角色名称和角色标识进行过滤。
     * 该接口用于权限管理页面显示已授权的角色列表。
     *
     * @param permissionId 权限ID，必填参数，用于指定要查询的权限
     * @param roleName 角色名称，可选参数，用于模糊匹配角色名称进行过滤
     * @param roleKey 角色标识，可选参数，用于模糊匹配角色标识进行过滤
     * @return 响应结果，包含拥有指定权限的角色列表
     *         <ul>
     *             <li>成功时返回角色列表，每个角色包含ID、名称、标识、状态、排序号和创建时间</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当权限ID为空时抛出
     * @see RoleAllVO 角色信息视图对象
     * @see SelPermTypeEnum#HAS_PERMISSION 查询类型：拥有权限
     */
    @PreAuthorize("hasAnyAuthority('system:permission:role:list')")
    @Parameters({
            @Parameter(name = "permissionId", description = "权限id"),
            @Parameter(name = "roleName", description = "角色名称"),
            @Parameter(name = "roleKey", description = "角色键")
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Operation(summary = "查询权限的角色列表")
    @GetMapping("/role/list")
    public ResponseResult<List<RoleAllVO>> selectPermissionIdRole(
            @NotNull Long permissionId,
            @RequestParam(required = false, name = "roleName") String roleName,
            @RequestParam(required = false, name = "roleKey") String roleKey
    ) {
        return ControllerUtils.messageHandler(() -> rolePermissionService
                .selectRoleByPermissionId(permissionId,roleName,roleKey, SelPermTypeEnum.HAS_PERMISSION.getType()));
    }

    /**
     * 查询没有指定权限的角色列表
     * <p>
     * 根据权限ID查询所有没有该权限的角色信息，支持按角色名称和角色标识进行过滤。
     * 该接口用于权限管理页面显示可授权的角色列表，便于管理员为角色分配权限。
     *
     * @param permissionId 权限ID，必填参数，用于指定要查询的权限
     * @param roleName 角色名称，可选参数，用于模糊匹配角色名称进行过滤
     * @param roleKey 角色标识，可选参数，用于模糊匹配角色标识进行过滤
     * @return 响应结果，包含没有指定权限的角色列表
     *         <ul>
     *             <li>成功时返回角色列表，每个角色包含ID、名称、标识、状态、排序号和创建时间</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当权限ID为空时抛出
     * @see RoleAllVO 角色信息视图对象
     * @see SelPermTypeEnum#NO_PERMISSION 查询类型：没有权限
     */
    @PreAuthorize("hasAnyAuthority('system:permission:role:not:list')")
    @Parameters({
            @Parameter(name = "permissionId", description = "权限id"),
            @Parameter(name = "roleName", description = "角色名称"),
            @Parameter(name = "roleKey", description = "角色键")
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Operation(summary = "查询没有该权限的角色列表")
    @GetMapping("/not/role/list")
    public ResponseResult<List<RoleAllVO>> selectPermissionNotRole(
            @NotNull Long permissionId,
            @RequestParam(required = false, name = "roleName") String roleName,
            @RequestParam(required = false, name = "roleKey") String roleKey
    ) {
        return ControllerUtils.messageHandler(() -> rolePermissionService
                .selectRoleByPermissionId(permissionId,roleName,roleKey,SelPermTypeEnum.NO_PERMISSION.getType()));
    }

    /**
     * 添加角色权限关系
     * <p>
     * 为指定的角色批量授予权限，支持多个角色同时授予多个权限。
     * 该操作会先删除已存在的相同角色权限关系，然后重新创建，确保数据的一致性。
     * 操作完成后会记录操作日志。
     *
     * @param rolePermissionDTO 角色权限关系数据传输对象，包含：
     *                         <ul>
     *                             <li>roleId: 角色ID列表，不能为空</li>
     *                             <li>permissionId: 权限ID列表，不能为空</li>
     *                         </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws IllegalArgumentException 当角色ID列表或权限ID列表为空时抛出
     * @see RolePermissionDTO 角色权限关系数据传输对象
     * @see LogConst#GRANT 操作类型：授权
     */
    @Operation(summary = "添加角色权限关系")
    @PreAuthorize("hasAnyAuthority('system:permission:role:add')")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Parameters({
            @Parameter(name = "rolePermissionDTO", description = "添加的数据")
    })
    @LogAnnotation(module="角色权限",operation= LogConst.GRANT)
    @PostMapping("/add")
    public ResponseResult<Void> addRolePermission(@Valid @RequestBody RolePermissionDTO rolePermissionDTO) {
        return rolePermissionService.addRolePermission(rolePermissionDTO);
    }

    /**
     * 删除角色权限关系
     * <p>
     * 批量取消指定角色的权限，支持同时取消多个角色的多个权限。
     * 该操作会删除角色权限关系表中对应的记录，操作完成后会记录操作日志。
     *
     * @param rolePermissionDTO 角色权限关系数据传输对象，包含：
     *                         <ul>
     *                             <li>roleId: 要取消权限的角色ID列表，不能为空</li>
     *                             <li>permissionId: 要取消的权限ID列表，不能为空</li>
     *                         </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示权限已成功取消</li>
     *             <li>失败时返回错误信息，可能是因为权限关系不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当角色ID列表或权限ID列表为空时抛出
     * @see RolePermissionDTO 角色权限关系数据传输对象
     * @see LogConst#DELETE 操作类型：删除
     */
    @Operation(summary = "删除角色权限关系")
    @PreAuthorize("hasAnyAuthority('system:permission:role:delete')")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @Parameters({
            @Parameter(name = "rolePermissionDTO", description = "删除的所需数据")
    })
    @LogAnnotation(module="角色权限",operation= LogConst.DELETE)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deleteRolePermission(@Valid @RequestBody RolePermissionDTO rolePermissionDTO) {
        return rolePermissionService.deleteRolePermission(rolePermissionDTO);
    }
}
