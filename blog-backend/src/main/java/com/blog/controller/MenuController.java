package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.MenuDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.MenuByIdVO;
import com.blog.domain.vo.MenuVO;
import com.blog.domain.vo.RoleVO;
import com.blog.service.MenuService;
import com.blog.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author haibara
 * @description 菜单相关接口
 * @since 2025/8/2 11:51
 */
@RestController
@Tag(name = "菜单相关接口")
@RequestMapping("menu")
@Validated
public class MenuController {

    @Resource
    private MenuService menuService;

    @Resource
    private RoleService roleService;

    /**
     * 获取管理菜单列表
     * <p>
     * 根据菜单类型获取系统菜单列表，支持路由菜单和管理菜单两种类型。
     * 该接口用于菜单管理页面显示完整的菜单列表。
     *
     * @param typeId 菜单类型ID，0表示系统菜单（路由菜单），1表示菜单管理
     * @return 响应结果，包含菜单列表
     *         <ul>
     *             <li>成功时返回菜单列表，每个菜单包含ID、标题、图标、路径等信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see MenuVO 菜单信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:menu:list')")
    @Operation(summary = "获取管理菜单列表")
    @Parameters({
            @Parameter(name = "typeId", description = "菜单类型id", required = true),
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/list/{typeId}")
    public ResponseResult<List<MenuVO>> list(@PathVariable("typeId") Integer typeId) {
        return menuService.getMenuList(typeId,null,null);
    }

    /**
     * 搜索管理菜单列表
     * <p>
     * 根据菜单类型、菜单标题和状态进行菜单搜索，支持模糊匹配和组合查询。
     * 该接口用于菜单管理页面的搜索功能，帮助管理员快速定位特定菜单。
     *
     * @param typeId 菜单类型ID，0表示系统菜单（路由菜单），1表示菜单管理
     * @param username 菜单标题，可选参数，用于模糊匹配菜单标题进行过滤
     * @param status 菜单状态，可选参数，用于精确匹配菜单状态进行过滤
     * @return 响应结果，包含符合搜索条件的菜单列表
     *         <ul>
     *             <li>成功时返回菜单列表，每个菜单包含ID、标题、图标、路径等信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see MenuVO 菜单信息视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('system:search:menu:list')")
    @Operation(summary = "搜索管理菜单列表")
    @Parameters({
            @Parameter(name = "typeId", description = "菜单类型id", required = true),
            @Parameter(name = "title", description = "菜单标题", required = true),
            @Parameter(name = "status", description = "状态", required = true)
    })
    @LogAnnotation(module="菜单管理",operation= LogConst.SEARCH)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/search/list/{typeId}")
    public ResponseResult<List<MenuVO>> searchMenu(@PathVariable("typeId") Integer typeId,String username,Integer status) {
        return menuService.getMenuList(typeId,username,status);
    }

    /**
     * 获取修改菜单角色列表
     * <p>
     * 获取系统中所有的角色信息，用于菜单管理页面的角色选择器。
     * 该接口在修改菜单时使用，为菜单分配相应的角色权限。
     *
     * @return 响应结果，包含所有角色列表
     *         <ul>
     *             <li>成功时返回角色列表，每个角色包含ID、角色名称、角色标识等信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see RoleVO 角色信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:menu:role:list')")
    @Operation(summary = "获取修改菜单角色列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.SELECT_UPDATE_MENU_ROLE_MAX_COUNT)
    @GetMapping("/role/list")
    public ResponseResult<List<RoleVO>> selectAll() {
        return roleService.selectAll();
    }

    /**
     * 获取路由菜单列表
     * <p>
     * 根据菜单类型获取路由菜单列表，主要用于前端路由生成。
     * 该接口会根据用户权限过滤菜单，只返回用户有权限访问的菜单。
     *
     * @param typeId 菜单类型ID，0表示系统菜单（路由菜单），1表示菜单管理
     * @return 响应结果，包含路由菜单列表
     *         <ul>
     *             <li>成功时返回菜单列表，每个菜单包含ID、标题、图标、路径等信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see MenuVO 菜单信息视图对象
     */
    @Operation(summary = "获取路由菜单列表")
    @Parameters({
            @Parameter(name = "typeId", description = "菜单类型id", required = true),
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/router/list/{typeId}")
    public ResponseResult<List<MenuVO>> routerList(@PathVariable("typeId") Integer typeId) {
        return menuService.getMenuList(typeId,null,null);
    }

    /**
     * 添加菜单
     * <p>
     * 创建新的系统菜单，支持多种菜单类型（普通组件、iframe、外部链接等）。
     * 该接口会根据路由类型自动设置相应的组件配置。
     *
     * @param menuDTO 菜单数据传输对象，包含菜单的所有配置信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see MenuDTO 菜单数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @PreAuthorize("hasAnyAuthority('system:menu:add')")
    @Operation(summary = "添加菜单")
    @Parameter(name = "menuDTO", description = "菜单信息", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="菜单管理",operation= LogConst.INSERT)
    @PostMapping
    public ResponseResult<Void> add(@RequestBody MenuDTO menuDTO) {
        return menuService.addMenu(menuDTO);
    }

    /**
     * 根据ID查询菜单信息
     * <p>
     * 根据菜单ID获取菜单的详细信息，用于菜单编辑页面的数据回显。
     * 返回的信息包括菜单基本信息和关联的角色列表。
     *
     * @param id 菜单ID，必须为有效的菜单标识
     * @return 响应结果，包含菜单详细信息
     *         <ul>
     *             <li>成功时返回菜单详细信息，包括基本信息和关联角色</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see MenuByIdVO 菜单详细信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:menu:select')")
    @Operation(summary = "根据id查询菜单信息")
    @Parameter(name = "id", description = "菜单id", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/{id}")
    public ResponseResult<MenuByIdVO> getById(@PathVariable("id") @NotNull Long id) {
        return menuService.selectUpdateMenu(id);
    }

    /**
     * 修改菜单
     * <p>
     * 更新现有菜单的配置信息，支持修改菜单的所有属性。
     * 该接口会根据路由类型自动调整相关配置，并更新菜单与角色的关联关系。
     *
     * @param menuDTO 菜单数据传输对象，包含要更新的菜单信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see MenuDTO 菜单数据传输对象
     * @see LogConst#UPDATE 操作类型：更新
     */
    @PreAuthorize("hasAnyAuthority('system:menu:update')")
    @Operation(summary = "修改菜单")
    @Parameter(name = "menuDTO", description = "菜单信息", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="菜单管理",operation= LogConst.UPDATE)
    @PutMapping
    public ResponseResult<Void> update(@RequestBody MenuDTO menuDTO) {
        return menuService.updateMenu(menuDTO);
    }

    /**
     * 根据ID删除菜单
     * <p>
     * 删除指定的菜单及其相关联的数据，包括角色菜单关系和权限信息。
     * 删除前会检查是否存在子菜单，如果存在子菜单则不允许删除。
     *
     * @param id 菜单ID，必须为有效的菜单标识
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功信息</li>
     *             <li>失败时返回错误信息，如存在子菜单时的提示</li>
     *         </ul>
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('system:menu:delete')")
    @Operation(summary = "根据id删除菜单")
    @Parameter(name = "id", description = "菜单id", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="菜单管理",operation= LogConst.DELETE)
    @DeleteMapping("/{id}")
    public ResponseResult<String> delete(@PathVariable("id") @NotNull Long id) {
        return menuService.deleteMenu(id);
    }

}
