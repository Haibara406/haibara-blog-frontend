package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.SuccessConst;
import com.blog.domain.dto.MenuDTO;
import com.blog.domain.entity.*;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.MenuByIdVO;
import com.blog.domain.vo.MenuVO;
import com.blog.enums.IsDisableEnum;
import com.blog.enums.MenuTypeEnum;
import com.blog.enums.RespEnum;
import com.blog.enums.RouterTypeEnum;
import com.blog.mapper.*;
import com.blog.service.MenuService;
import com.blog.service.RoleMenuService;
import com.blog.utils.SecurityUtils;
import com.blog.utils.ServiceUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 系统菜单服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private RoleMenuService roleMenuService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 获取菜单列表,0:系统菜单，1:菜单管理
     *
     * @param typeId 菜单类型id
     * @param title 菜单标题
     * @param status 状态
     * @return 菜单列表
     */
    @Override
    public ResponseResult<List<MenuVO>> getMenuList(Integer typeId, String title, Integer status) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getOrderNum);

        // 路由菜单
        if (Objects.equals(typeId, MenuTypeEnum.ROUTE_MENU.getType())) {
            Long userId = SecurityUtils.getUserId();

            // 优化：一次性获取用户可访问的菜单ID
            Set<Long> accessibleMenuIds = getUserAccessibleMenuIds(userId);

            if (!accessibleMenuIds.isEmpty()) {
                wrapper.in(Menu::getId, accessibleMenuIds);
            }
            wrapper.eq(Menu::getIsDisable, IsDisableEnum.NOT_DISABLE.getValue());
        }

        if (Objects.equals(typeId, MenuTypeEnum.MANAGE_MENU.getType()) && (title != null || status != null)) {
            wrapper.eq(status != null, Menu::getIsDisable, status)
                    .and(o -> o.like(Menu::getTitle, title));
        }

        List<Menu> menus = menuMapper.selectList(wrapper);
        List<MenuVO> list = menus.stream()
                .map(this::convertToMenuVO)
                .toList();

        return ResponseResult.success(list);
    }

    /**
     * 添加菜单
     *
     * @param menuDTO 菜单信息
     * @return 添加菜单的结果
     */
    @Override
    public ResponseResult<Void> addMenu(MenuDTO menuDTO) {
        if (menuDTO.getRouterType() == null) {
            menuDTO.setRouterType(RouterTypeEnum.NORMAL.getType().intValue());
        }
        switch (menuDTO.getRouterType()) {
            // 普通组件
            case 0 -> {
                if (Objects.equals(menuDTO.getComponent(), "")) {
                    menuDTO.setComponent(RouterTypeEnum.NORMAL.getComponent());
                }
            }
            case 1 -> menuDTO.setComponent(RouterTypeEnum.IFRAME.getComponent());
            case 2 -> menuDTO.setComponent(RouterTypeEnum.EXTERNAL.getComponent());
        }

        Menu menu = menuDTO.asViewObject(Menu.class);
        if (this.save(menu)) {
            log.info(SuccessConst.MENU_ADD_SUCCESSFUL);
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 修改菜单，id回滚
     *
     * @param id 菜单id
     * @return 数据
     */
    @Override
    public ResponseResult<MenuByIdVO> selectUpdateMenu(Long id) {
        Menu menu = this.getById(id);

        // 优化：直接获取角色ID，避免不必要的Role实体查询
        List<Long> roleIds = roleMenuMapper
                .selectList(new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getMenuId, menu.getId())
                        .select(RoleMenu::getRoleId))
                .stream()
                .map(RoleMenu::getRoleId)
                .toList();

        MenuByIdVO vo = menu.asViewObject(MenuByIdVO.class, v -> {
            // 根据组件类型设置路由类型
            setRouterType(v);
            // 设置角色ID列表
            v.setRoleId(roleIds.isEmpty() ? null : roleIds);
        });

        return ResponseResult.success(vo);
    }

    /**
     * 修改菜单
     *
     * @param menuDTO 菜单信息
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> updateMenu(MenuDTO menuDTO) {
        LambdaUpdateWrapper<Menu> wrapper = new LambdaUpdateWrapper<>();
        if (menuDTO.getRouterType() == RouterTypeEnum.DIRECTORY.getType().intValue()) {
            // 父菜单
            menuDTO.setComponent(RouterTypeEnum.DIRECTORY.getComponent());
        }
        if (menuDTO.getRouterType() == RouterTypeEnum.NORMAL.getType().intValue()) {
            wrapper.set(Menu::getRedirect,null);
        }
        if (menuDTO.getRouterType() == RouterTypeEnum.NORMAL.getType().intValue()
                || menuDTO.getRouterType() == RouterTypeEnum.DIRECTORY.getType().intValue()) {
            wrapper.set(Menu::getUrl, null);
        }
        if (menuDTO.getRouterType() == RouterTypeEnum.IFRAME.getType().intValue()) {
            menuDTO.setComponent(RouterTypeEnum.IFRAME.getComponent());
            wrapper.set(Menu::getTarget, null);
        }
        if (menuDTO.getRouterType() == RouterTypeEnum.EXTERNAL.getType().intValue()) {
            wrapper.set(Menu::getComponent, RouterTypeEnum.EXTERNAL.getComponent());
            wrapper.set(Menu::getRedirect, null);
        }
        if (menuDTO.getParentId() == null) {
            wrapper.set(Menu::getParentId,null);
        }

        Menu menu = menuDTO.asViewObject(Menu.class);

        // 是否有关联关系
        Long count = roleMenuMapper.selectCount(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, menu.getId()));
        if (count > 0) {
            // 删除所有的角色关联
            roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, menu.getId()));
        }
        if (menuDTO.getRoleId() != null) {
            List<RoleMenu> roleMenus = menuDTO.getRoleId().stream().map(roleId -> new RoleMenu(roleId, menu.getId())).toList();
            // 新增角色关联
            roleMenuService.saveBatch(roleMenus);
        }

        if (menuMapper.update(menu, wrapper.eq(Menu::getId, menu.getId())) > 0) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 根据id删除菜单
     *
     * @param id 对应的id
     * @return 是否成功
     */
    @Override
    @Transactional
    public ResponseResult<String> deleteMenu(Long id) {
        // 判断是否有未删除的子目录
        if (menuMapper.selectCount(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id)) > 0) {
            return ResponseResult.failure(RespEnum.NO_DELETE_CHILD_MENU.getCode(),RespEnum.NO_DELETE_CHILD_MENU.getMsg());
        }
        if (this.removeById(id)) {
            // 删除相关菜单角色关系
            roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, id));
            // 删除菜单对应权限
            permissionMapper.delete(new LambdaQueryWrapper<Permission>().eq(Permission::getMenuId, id));
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 优化后的方法：获取用户可访问的菜单ID
     * 避免多次全表查询
     *
     * @param userId 用户ID
     * @return 用户可访问的菜单ID集合
     */
    private Set<Long> getUserAccessibleMenuIds(Long userId) {
        Set<Long> accessibleMenuIds = new HashSet<>();

        // 1. 获取用户的角色ID（只查询需要的字段）
        List<Long> userRoleIds = userRoleMapper
                .selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .select(UserRole::getRoleId))
                .stream()
                .map(UserRole::getRoleId)
                .toList();

        // 2. 如果用户有角色，获取角色对应的菜单ID
        if (!userRoleIds.isEmpty()) {
            List<Long> roleMenuIds = roleMenuMapper
                    .selectList(new LambdaQueryWrapper<RoleMenu>()
                            .in(RoleMenu::getRoleId, userRoleIds)
                            .select(RoleMenu::getMenuId))
                    .stream()
                    .map(RoleMenu::getMenuId)
                    .toList();
            accessibleMenuIds.addAll(roleMenuIds);
        }

        // 3. 获取所有已分配权限的菜单ID（避免全表查询Menu）
        Set<Long> assignedMenuIds = roleMenuMapper
                .selectList(new LambdaQueryWrapper<RoleMenu>()
                        .select(RoleMenu::getMenuId))
                .stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 4. 获取未分配权限的菜单ID（这些菜单所有用户都可以访问）
        List<Long> unassignedMenuIds = menuMapper
                .selectList(new LambdaQueryWrapper<Menu>()
                        .notIn(!assignedMenuIds.isEmpty(), Menu::getId, assignedMenuIds)
                        .select(Menu::getId))
                .stream()
                .map(Menu::getId)
                .toList();

        accessibleMenuIds.addAll(unassignedMenuIds);

        return accessibleMenuIds;
    }

    /**
     * 提取菜单转换逻辑
     *
     * @param menu 菜单实体
     * @return 菜单VO
     */
    private MenuVO convertToMenuVO(Menu menu) {
        return MenuVO.builder()
                .id(menu.getId())
                .title(menu.getTitle())
                .icon(menu.getIcon())
                .path(menu.getPath())
                .component(menu.getComponent())
                .redirect(menu.getRedirect())
                .affix(ServiceUtil.isFalseOrTrue(menu.getAffix()))
                .parentId(menu.getParentId())
                .name(menu.getName())
                .hideInMenu(ServiceUtil.isFalseOrTrue(menu.getHideInMenu()))
                .url(menu.getUrl())
                .keepAlive(ServiceUtil.isFalseOrTrue(menu.getKeepAlive()))
                .target(menu.getTarget())
                .isDisable(!ServiceUtil.isFalseOrTrue(menu.getIsDisable()))
                .orderNum(menu.getOrderNum())
                .createTime(menu.getCreateTime())
                .build();
    }

    /**
     * 提取路由类型设置逻辑
     *
     * @param vo 菜单VO
     */
    private void setRouterType(MenuByIdVO vo) {
        if (vo.getComponent() == null) {
            vo.setRouterType(RouterTypeEnum.EXTERNAL.getType());
        } else if (RouterTypeEnum.IFRAME.getComponent().equals(vo.getComponent())) {
            vo.setRouterType(RouterTypeEnum.IFRAME.getType());
        } else {
            vo.setRouterType(RouterTypeEnum.NORMAL.getType());
        }
    }
}
