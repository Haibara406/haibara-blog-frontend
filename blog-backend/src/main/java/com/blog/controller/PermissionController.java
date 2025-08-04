package com.blog.controller;

import com.blog.annotation.LogAnnotation;
import com.blog.constants.LogConst;
import com.blog.constants.ValidationConstants;
import com.blog.domain.dto.PermissionDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PermissionMenuVO;
import com.blog.domain.vo.PermissionVO;
import com.blog.service.PermissionService;
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
 * 权限管理控制器
 *
 * @author haibara
 * @description 权限字符相关接口
 * @since 2025/7/29 13:31
 */
@RestController
@Tag(name = "权限字符相关接口")
@RequestMapping("permission")
@Validated
public class PermissionController {


    @Resource
    private PermissionService permissionService;

    /**
     * 查询所有权限列表
     * <p>
     * 获取系统中所有的权限信息，包括权限ID、权限描述、权限标识和所属菜单名称。
     * 该接口用于权限管理页面显示完整的权限列表。
     *
     * @return 响应结果，包含所有权限列表
     *         <ul>
     *             <li>成功时返回权限列表，每个权限包含ID、描述、标识和菜单名称</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PermissionVO 权限信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:permission:list')")
    @Operation(summary = "所有权限")
    @GetMapping("/list")
    public ResponseResult<List<PermissionVO>> list() {
        return ResponseResult.success(permissionService.selectPermission(null, null, null));
    }

    /**
     * 搜索权限
     * <p>
     * 根据权限描述、权限标识或所属菜单ID进行权限搜索，支持模糊匹配和组合查询。
     * 该接口用于权限管理页面的搜索功能，帮助管理员快速定位特定权限。
     *
     * @param permissionDesc 权限描述，可选参数，用于模糊匹配权限描述进行过滤
     * @param permissionKey 权限标识，可选参数，用于模糊匹配权限标识进行过滤
     * @param permissionMenuId 权限所属菜单ID，可选参数，用于精确匹配菜单进行过滤
     * @return 响应结果，包含符合搜索条件的权限列表
     *         <ul>
     *             <li>成功时返回权限列表，每个权限包含ID、描述、标识和菜单名称</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PermissionVO 权限信息视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('system:permission:search')")
    @Operation(summary = "搜索权限")
    @Parameters({
            @Parameter(name = "permissionDesc", description = "权限字符描述"),
            @Parameter(name = "permissionKey", description = "权限字符键"),
            @Parameter(name = "permissionMenuId", description = "权限字符所属菜单")
    })
    @LogAnnotation(module="权限管理",operation= LogConst.SEARCH)
    @GetMapping("/search")
    public ResponseResult<List<PermissionVO>> searchPermission(
            @RequestParam(value = "permissionDesc", required = false) String permissionDesc,
            @RequestParam(value = "permissionKey", required = false) String permissionKey,
            @RequestParam(value = "permissionMenuId", required = false) Long permissionMenuId
    ) {
        return ResponseResult.success(permissionService.selectPermission(permissionDesc, permissionKey, permissionMenuId));
    }

    /**
     * 查询所有权限所在菜单
     * <p>
     * 获取系统中所有权限所属的菜单信息，用于权限管理页面的菜单选择器。
     * 返回的菜单列表已去重，每个菜单只显示一次。
     *
     * @return 响应结果，包含所有权限所在的菜单列表
     *         <ul>
     *             <li>成功时返回菜单列表，每个菜单包含权限ID、菜单名称和菜单ID</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PermissionMenuVO 权限菜单信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:permission:menu:list')")
    @Operation(summary = "查询所有权限所在菜单")
    @GetMapping("/menu")
    public ResponseResult<List<PermissionMenuVO>> menu() {
        return ControllerUtils.messageHandler(() -> permissionService.selectPermissionMenu());
    }

    /**
     * 添加权限字符
     * <p>
     * 创建新的权限字符，包括权限描述、权限标识和所属菜单。
     * 系统会自动检查权限标识的唯一性，防止重复创建相同的权限。
     * 操作完成后会记录操作日志。
     *
     * @param permissionDTO 权限数据传输对象，包含：
     *                     <ul>
     *                         <li>permissionDesc: 权限描述，不能为空</li>
     *                         <li>permissionKey: 权限标识，不能为空，必须唯一</li>
     *                         <li>permissionMenuId: 所属菜单ID，不能为空</li>
     *                     </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为权限标识重复或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当必填字段为空时抛出
     * @see PermissionDTO 权限数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @Operation(summary = "添加权限字符")
    @Parameter(name = "permissionDTO", description = "权限字符信息")
    @PreAuthorize("hasAnyAuthority('system:permission:add')")
    @LogAnnotation(module="权限管理",operation= LogConst.INSERT)
    @PostMapping("/add")
    public ResponseResult<Void> addPermission(@RequestBody @Valid PermissionDTO permissionDTO) {
        return permissionService.updateOrInsertPermission(permissionDTO.setId(null));
    }

    /**
     * 修改权限字符
     * <p>
     * 更新现有权限的信息，包括权限描述、权限标识和所属菜单。
     * 系统会自动检查权限标识的唯一性，确保修改后的权限标识不与其他权限冲突。
     * 操作完成后会记录操作日志。
     *
     * @param permissionDTO 权限数据传输对象，包含：
     *                     <ul>
     *                         <li>id: 权限ID，不能为空，用于标识要修改的权限</li>
     *                         <li>permissionDesc: 权限描述，不能为空</li>
     *                         <li>permissionKey: 权限标识，不能为空，必须唯一</li>
     *                         <li>permissionMenuId: 所属菜单ID，不能为空</li>
     *                     </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为权限标识重复或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当必填字段为空时抛出
     * @see PermissionDTO 权限数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @Operation(summary = "修改权限字符")
    @Parameter(name = "permissionDTO", description = "权限字符信息")
    @PreAuthorize("hasAnyAuthority('system:permission:update')")
    @LogAnnotation(module="权限管理",operation= LogConst.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> updatePermission(@RequestBody @Valid PermissionDTO permissionDTO) {
        return permissionService.updateOrInsertPermission(permissionDTO);
    }

    /**
     * 获取要修改的权限信息
     * <p>
     * 根据权限ID获取权限的详细信息，用于权限编辑页面的数据回显。
     * 返回的数据包含权限的所有可编辑字段。
     *
     * @param id 权限ID，必填参数，用于指定要获取的权限
     * @return 响应结果，包含权限的详细信息
     *         <ul>
     *             <li>成功时返回权限信息，包含ID、描述、标识和所属菜单ID</li>
     *             <li>失败时返回错误信息，可能是因为权限不存在</li>
     *         </ul>
     * @throws IllegalArgumentException 当权限ID为空时抛出
     * @see PermissionDTO 权限数据传输对象
     */
    @Operation(summary = "获取要修改的权限信息")
    @Parameter(name = "id", description = "权限字符id")
    @PreAuthorize("hasAnyAuthority('system:permission:get')")
    @GetMapping("/get/{id}")
    public ResponseResult<PermissionDTO> getPermission(
            @PathVariable("id") @NotNull(message = ValidationConstants.PERMISSION_ID_NOT_NULL) Long id
    ) {
        return ControllerUtils.messageHandler(() -> permissionService.getPermission(id));
    }

    /**
     * 删除权限字符
     * <p>
     * 根据权限ID删除指定的权限，同时会删除所有与该权限相关的角色权限关系。
     * 该操作不可逆，请谨慎使用。操作完成后会记录操作日志。
     *
     * @param id 权限ID，必填参数，用于指定要删除的权限
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示权限已成功删除</li>
     *             <li>失败时返回错误信息，可能是因为权限不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当权限ID为空时抛出
     * @see LogConst#DELETE 操作类型：删除
     */
    @Operation(summary = "删除权限字符")
    @Parameter(name = "id", description = "权限字符id")
    @PreAuthorize("hasAnyAuthority('system:permission:delete')")
    @LogAnnotation(module="权限管理",operation= LogConst.DELETE)
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> deletePermission(
            @PathVariable("id") @NotNull(message = ValidationConstants.PERMISSION_ID_NOT_NULL) Long id
    ) {
        return permissionService.deletePermission(id);
    }

}
