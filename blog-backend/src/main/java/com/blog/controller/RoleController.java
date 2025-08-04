package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.RoleDTO;
import com.blog.domain.dto.RoleDeleteDTO;
import com.blog.domain.dto.RoleSearchDTO;
import com.blog.domain.dto.UpdateRoleStatusDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.RoleAllVO;
import com.blog.domain.vo.RoleByIdVO;
import com.blog.service.RoleService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色管理控制器
 *
 * @author haibara
 * @description 角色相关接口
 * @since 2025/7/28 23:20
 */
@RestController
@Tag(name = "角色相关接口")
@RequestMapping("role")
@Validated
public class RoleController {


    @Resource
    private RoleService roleService;

    /**
     * 获取系统角色列表
     * <p>
     * 查询系统中所有的角色信息，按排序号升序排列显示。
     * 该接口用于角色管理页面的初始数据加载和角色选择组件。
     * 操作完成后会记录操作日志。
     *
     * @return 响应结果，包含完整的角色列表
     *         <ul>
     *             <li>成功时返回角色列表，每个角色包含：
     *                 <ul>
     *                     <li>角色ID和角色名称</li>
     *                     <li>角色标识（roleKey）</li>
     *                     <li>角色状态（0-正常，1-停用）</li>
     *                     <li>排序号和创建时间</li>
     *                 </ul>
     *             </li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see RoleAllVO 角色列表视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:role:list')")
    @Operation(summary = "获取角色列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/list")
    public ResponseResult<List<RoleAllVO>> selectAllRole() {
        return ControllerUtils.messageHandler(() -> roleService.selectRole(null));
    }


    /**
     * 更新角色状态
     * <p>
     * 修改指定角色的启用/禁用状态，用于角色的激活控制。
     * 禁用的角色将无法分配给用户，已分配的用户权限也会受到影响。
     * 操作完成后会记录操作日志。
     *
     * @param updateRoleStatusDTO 角色状态更新数据传输对象，包含：
     *                           <ul>
     *                               <li>id: 要更新的角色ID，必填</li>
     *                               <li>status: 新的状态值，必填
     *                                   <ul>
     *                                       <li>0: 正常（启用）</li>
     *                                       <li>1: 停用（禁用）</li>
     *                                   </ul>
     *                               </li>
     *                           </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是角色不存在或权限不足</li>
     *         </ul>
     * @see UpdateRoleStatusDTO 角色状态更新数据传输对象
     * @see LogConst#UPDATE 操作类型：更新
     */
    @PreAuthorize("hasAnyAuthority('system:role:status:update')")
    @Operation(summary = "更新角色状态")
    @Parameter(name = "roleDeleteDTO", description = "修改角色状态")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="角色管理",operation=LogConst.UPDATE)
    @PostMapping("/update/status")
    public ResponseResult<Void> updateStatus(@RequestBody @Valid UpdateRoleStatusDTO updateRoleStatusDTO) {
        return roleService.updateStatus(updateRoleStatusDTO.getId(),updateRoleStatusDTO.getStatus());
    }

    /**
     * 根据ID获取角色详细信息
     * <p>
     * 查询指定角色的详细信息，用于角色编辑页面的数据回显。
     * 返回的信息包含角色的所有属性和关联的菜单权限列表。
     * 操作完成后会记录操作日志。
     *
     * @param id 角色ID，必填参数
     *          <ul>
     *              <li>必须是有效的角色ID</li>
     *              <li>角色必须存在且未被删除</li>
     *          </ul>
     * @return 响应结果，包含角色详细信息
     *         <ul>
     *             <li>成功时返回角色详细信息，包含：
     *                 <ul>
     *                     <li>基本信息：ID、名称、标识、状态、排序号、备注</li>
     *                     <li>关联权限：该角色拥有的菜单权限ID列表</li>
     *                 </ul>
     *             </li>
     *             <li>失败时返回错误信息，可能是角色不存在</li>
     *         </ul>
     * @see RoleByIdVO 角色详细信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:role:get')")
    @Operation(summary = "获取修改角色信息")
    @Parameter(name = "id", description = "角色id")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/get/{id}")
    public ResponseResult<RoleByIdVO> selectAll(@PathVariable("id") @NotNull Long id) {
        return roleService.selectRoleById(id);
    }

    /**
     * 修改角色信息
     * <p>
     * 更新指定角色的基本信息和权限配置，支持修改角色的所有属性。
     * 该操作会同时更新角色与菜单权限的关联关系。
     * 操作完成后会记录操作日志。
     *
     * @param roleDTO 角色修改数据传输对象，包含：
     *               <ul>
     *                   <li>id: 角色ID，必填（用于标识要修改的角色）</li>
     *                   <li>roleName: 角色名称，必填</li>
     *                   <li>roleKey: 角色标识，必填（系统内唯一）</li>
     *                   <li>status: 角色状态，必填（0-正常，1-停用）</li>
     *                   <li>orderNum: 排序号，必填</li>
     *                   <li>remark: 备注信息，可选</li>
     *                   <li>menuIds: 关联的菜单权限ID列表，可选</li>
     *               </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>角色不存在</li>
     *                     <li>角色标识已被其他角色使用</li>
     *                     <li>权限不足</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see RoleDTO 角色数据传输对象
     * @see LogConst#UPDATE 操作类型：更新
     */
    @PreAuthorize("hasAnyAuthority('system:role:update')")
    @Operation(summary = "修改角色信息")
    @Parameter(name = "roleDTO", description = "修改角色所需数据")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="角色管理",operation= LogConst.UPDATE)
    @PutMapping("/update")
    public ResponseResult<Void> updateRole(@RequestBody @Valid RoleDTO roleDTO) {
        return roleService.updateOrInsertRole(roleDTO);
    }

    /**
     * 添加新角色
     * <p>
     * 创建新的系统角色，包括基本信息设置和权限配置。
     * 新角色创建后可以分配给用户，用户将获得该角色对应的系统权限。
     * 操作完成后会记录操作日志。
     *
     * @param roleDTO 角色添加数据传输对象，包含：
     *               <ul>
     *                   <li>roleName: 角色名称，必填</li>
     *                   <li>roleKey: 角色标识，必填（系统内必须唯一）</li>
     *                   <li>status: 角色状态，必填（0-正常，1-停用）</li>
     *                   <li>orderNum: 排序号，必填</li>
     *                   <li>remark: 备注信息，可选</li>
     *                   <li>menuIds: 关联的菜单权限ID列表，可选</li>
     *               </ul>
     *               注意：ID字段会被自动设置为null，确保创建新记录
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>角色名称或标识已存在</li>
     *                     <li>必填字段缺失</li>
     *                     <li>权限不足</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see RoleDTO 角色数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @PreAuthorize("hasAnyAuthority('system:role:add')")
    @Operation(summary = "添加角色信息")
    @Parameter(name = "roleDTO", description = "添加角色所需数据")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="角色管理",operation= LogConst.INSERT)
    @PutMapping("/add")
    public ResponseResult<Void> addRole(@RequestBody @Valid RoleDTO roleDTO) {
        return roleService.updateOrInsertRole(roleDTO.setId(null));
    }

    /**
     * 批量删除角色
     * <p>
     * 根据提供的角色ID列表批量删除角色记录，支持同时删除多个角色。
     * 删除角色前会检查是否有用户正在使用该角色，如有关联则无法删除。
     * 删除操作会同时清理角色相关的权限关联关系。
     * 操作完成后会记录操作日志。
     *
     * @param roleDeleteDTO 角色删除数据传输对象，包含：
     *                     <ul>
     *                         <li>ids: 要删除的角色ID列表，必填且不能为空</li>
     *                     </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示删除操作完成</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>角色不存在</li>
     *                     <li>角色正在被用户使用</li>
     *                     <li>权限不足</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see RoleDeleteDTO 角色删除数据传输对象
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('system:role:delete')")
    @Operation(summary = "根据id删除角色")
    @Parameter(name = "roleDeleteDTO", description = "id数组")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="角色管理",operation=LogConst.DELETE)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deleteRole(@RequestBody @Valid RoleDeleteDTO roleDeleteDTO) {
        return roleService.deleteRole(roleDeleteDTO.getIds());
    }

    /**
     * 根据条件搜索角色
     * <p>
     * 根据指定的搜索条件筛选角色记录，支持多种条件组合查询。
     * 该接口用于角色管理页面的高级搜索功能，帮助管理员快速定位特定的角色。
     * 操作完成后会记录操作日志。
     *
     * @param roleSearchDTO 角色搜索条件数据传输对象，包含：
     *                     <ul>
     *                         <li>roleName: 角色名称关键词搜索，支持模糊匹配</li>
     *                         <li>roleKey: 角色标识关键词搜索，支持模糊匹配</li>
     *                         <li>status: 角色状态筛选（0-正常，1-停用）</li>
     *                         <li>createTimeStart: 创建时间范围开始</li>
     *                         <li>createTimeEnd: 创建时间范围结束</li>
     *                     </ul>
     * @return 响应结果，包含符合条件的角色列表
     *         <ul>
     *             <li>成功时返回筛选后的角色列表，按排序号升序排列</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see RoleSearchDTO 角色搜索条件数据传输对象
     * @see RoleAllVO 角色列表视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('system:role:search')")
    @Operation(summary = "根据条件搜索角色")
    @Parameter(name = "roleSearchDTO", description = "搜索条件")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="角色管理",operation= LogConst.SEARCH)
    @PostMapping("/search")
    public ResponseResult<List<RoleAllVO>> searchRole(@RequestBody RoleSearchDTO roleSearchDTO) {
        return ControllerUtils.messageHandler(() -> roleService.selectRole(roleSearchDTO));
    }

}
