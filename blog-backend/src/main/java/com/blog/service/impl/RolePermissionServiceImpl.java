package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.domain.dto.RolePermissionDTO;
import com.blog.domain.entity.Role;
import com.blog.domain.entity.RolePermission;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.RoleAllVO;
import com.blog.enums.SelPermTypeEnum;
import com.blog.mapper.RoleMapper;
import com.blog.mapper.RolePermissionMapper;
import com.blog.service.RolePermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author haibara
 * @description 角色权限关系服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("rolePermissionService")
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {


    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 根据权限查询角色
     *
     * @param permissionId 权限id
     * @param roleName     角色名称
     * @param roleKey      角色字符
     * @param type         角色类型,0：该角色的使用用户 1：该角色的未使用用户
     * @return 角色列表
     */
    @Override
    public List<RoleAllVO> selectRoleByPermissionId(Long permissionId, String roleName, String roleKey, Integer type) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getPermissionId, permissionId);
        List<Long> roleIds;
        if (Objects.equals(type, SelPermTypeEnum.HAS_PERMISSION.getType())) {
            // 查询拥有权限的角色
            roleIds = rolePermissionMapper.selectList(wrapper).stream().map(RolePermission::getRoleId).toList();
        } else {
            // 查询拥有权限的角色
            roleIds = rolePermissionMapper.selectList(wrapper).stream().map(RolePermission::getRoleId).toList();
            // 查询没有该权限的角色
            roleIds = roleMapper.selectList(new LambdaQueryWrapper<Role>().notIn(!roleIds.isEmpty(), Role::getId, roleIds))
                    .stream().map(Role::getId).toList();
        }

        if (!roleIds.isEmpty()) {
            // 批量查询角色信息
            LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Role::getId, roleIds);

            if (Objects.nonNull(roleName)) {
                queryWrapper.like(Role::getRoleName, roleName);
            }
            if (Objects.nonNull(roleKey)) {
                queryWrapper.like(Role::getRoleKey, roleKey);
            }

            List<Role> roleList = roleMapper.selectList(queryWrapper);
            return roleList.stream().map(role -> role.asViewObject(RoleAllVO.class)).toList();
        }
        return null;
    }

    /**
     * 给多个角色添加某个权限
     *
     * @param rolePermissionDTO 角色权限数据
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> addRolePermission(RolePermissionDTO rolePermissionDTO) {
        List<Long> roleIds = rolePermissionDTO.getRoleId();
        List<Long> permissionIds = rolePermissionDTO.getPermissionId();
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RolePermission::getPermissionId, permissionIds)
                .in(RolePermission::getRoleId, roleIds);
        // 如有，先删除
        if (rolePermissionMapper.selectCount(wrapper) > 0) rolePermissionMapper.delete(wrapper);

        List<RolePermission> rolePermissions = new ArrayList<>();
        roleIds.forEach(roleId -> {
            permissionIds.forEach(permissionId -> {
                rolePermissions.add(RolePermission.builder().roleId(roleId).permissionId(permissionId).build());
            });
        });
        if (saveBatch(rolePermissions)) return ResponseResult.success();

        return ResponseResult.failure();
    }

    /**
     * 批量或单个取消授权
     *
     * @param rolePermissionDTO 角色权限数据
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> deleteRolePermission(RolePermissionDTO rolePermissionDTO) {
        int isDelete = rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getPermissionId, rolePermissionDTO.getPermissionId()).in(RolePermission::getRoleId, rolePermissionDTO.getRoleId()));
        if (isDelete > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }
}
