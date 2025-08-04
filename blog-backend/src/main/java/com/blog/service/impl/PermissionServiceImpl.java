package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.ErrorConst;
import com.blog.domain.dto.PermissionDTO;
import com.blog.domain.entity.Menu;
import com.blog.domain.entity.Permission;
import com.blog.domain.entity.RolePermission;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PermissionMenuVO;
import com.blog.domain.vo.PermissionVO;
import com.blog.mapper.MenuMapper;
import com.blog.mapper.PermissionMapper;
import com.blog.mapper.RolePermissionMapper;
import com.blog.service.PermissionService;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 系统权限服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("permissionService")
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private MenuMapper menuMapper;


    /**
     * 查询权限
     * @param permissionDesc 权限描述
     * @param permissionKey 权限字符
     * @param permissionMenuId 权限字符所属菜单id
     * @return 权限
     */
    @Override
    public List<PermissionVO> selectPermission(String permissionDesc, String permissionKey, Long permissionMenuId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(permissionDesc), Permission::getPermissionDesc, permissionDesc)
                .like(Objects.nonNull(permissionKey), Permission::getPermissionKey, permissionKey)
                .eq(Objects.nonNull(permissionMenuId), Permission::getMenuId, permissionMenuId);
        List<Permission> permissions = permissionMapper.selectList(wrapper);

        if (!permissions.isEmpty()) {
            // 批量查询菜单并构建Map
            List<Long> menuIds = permissions.stream().map(Permission::getMenuId).toList();
            Map<Long, Menu> menuMap = menuMapper.selectBatchIds(menuIds)
                    .stream()
                    .collect(Collectors.toMap(Menu::getId, menu -> menu));

            return permissions.stream().map(permission -> permission.asViewObject(PermissionVO.class, v -> {
                // 使用Map查找替代stream.filter
                Menu menu = menuMap.get(permission.getMenuId());
                if (menu != null) {
                    v.setMenuName(menu.getTitle());
                }
            })).toList();
        }
        return new ArrayList<>();
    }

    /**
     * 查询权限菜单
     * @return 权限所在菜单
     */
    @Override
    public List<PermissionMenuVO> selectPermissionMenu() {
        List<Permission> permissions = permissionMapper.selectList(null);
        if (!permissions.isEmpty()) {
            // 批量查询菜单并构建Map
            List<Long> menuIds = permissions.stream().map(Permission::getMenuId).toList();
            Map<Long, Menu> menuMap = menuMapper.selectBatchIds(menuIds)
                    .stream()
                    .collect(Collectors.toMap(Menu::getId, menu -> menu));

            List<PermissionMenuVO> vos = permissions.stream().map(permission -> permission.asViewObject(PermissionMenuVO.class, v -> {
                // 使用Map查找替代stream.filter
                Menu menu = menuMap.get(permission.getMenuId());
                if (menu != null) {
                    v.setMenuName(menu.getTitle());
                    v.setMenuId(menu.getId());
                }
            })).toList();
            return vos.stream().distinct().toList();
        }
        return new ArrayList<>();
    }

    /**
     * 修改或添加
     * @param permissionDTO 权限DTO
     * @return  修改或添加权限
     */
    @Transactional
    @Override
    public ResponseResult<Void> updateOrInsertPermission(PermissionDTO permissionDTO) {
        // 权限字符是否重复
        Permission isPermission = permissionMapper.selectOne(new LambdaQueryWrapper<Permission>().eq(Permission::getPermissionKey, permissionDTO.getPermissionKey().trim()));
        if (StringUtils.isNotNull(isPermission) && !isPermission.getId().equals(permissionDTO.getId())) {
            return ResponseResult.failure(ErrorConst.PERM_KEY_CANNOT_BE_REPEAT);
        }
        Permission permission = permissionDTO.asViewObject(Permission.class, v -> v.setMenuId(permissionDTO.getPermissionMenuId()));
        if (this.saveOrUpdate(permission)) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 获取修改的权限信息
     * @param id 权限id
     * @return 需要的信息
     */
    @Override
    public PermissionDTO getPermission(Long id) {
        Permission permission = getById(id);
        return permission.asViewObject(PermissionDTO.class, v -> v.setPermissionMenuId(permission.getMenuId()));
    }

    /**
     * 删除权限
     * @param id 权限id
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> deletePermission(Long id) {
        if (permissionMapper.deleteById(id) > 0) {
            // 删除关系
            rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getPermissionId, id));
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }
}
