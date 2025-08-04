package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.domain.dto.RoleUserDTO;
import com.blog.domain.vo.RoleUserVO;
import com.blog.domain.dto.UserRoleDTO;
import com.blog.domain.entity.Role;
import com.blog.domain.entity.User;
import com.blog.domain.entity.UserRole;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.RoleAllVO;
import com.blog.enums.SelRoleUserEnum;
import com.blog.enums.SelUserRoleEnum;
import com.blog.mapper.RoleMapper;
import com.blog.mapper.UserMapper;
import com.blog.mapper.UserRoleMapper;
import com.blog.service.UserRoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author haibara
 * @description 用户角色关系服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    /**
     * @param roleId   角色id
     * @param username 用户名
     * @param email    邮箱
     * @param type     角色类型,0：该角色的使用用户 1：该角色的未使用用户
     * @return 角色用户列表
     */
    @Override
    public List<RoleUserVO> selectRoleUser(Long roleId, String username, String email, Integer type) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleId,roleId);
        List<Long> userIds;
        if(Objects.equals(type, SelRoleUserEnum.HAS_ROLE.getType())){
            // 查询存在角色的用户id
            userIds = userRoleMapper.selectList(wrapper).stream().map(UserRole::getUserId).toList();
        }else{
            // 查询存在角色的用户id
            userIds = userRoleMapper.selectList(wrapper).stream().map(UserRole::getUserId).toList();
            // 查询没有该角色的用户
            userIds = userMapper.selectList(new LambdaQueryWrapper<User>().notIn(!userIds.isEmpty(),User::getId, userIds)).stream().map(User::getId).toList();
        }
        if (!userIds.isEmpty()) {
            // 使用批量查询替代循环查询
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(User::getId, userIds);

            // 添加搜索条件
            if (Objects.nonNull(username)) {
                queryWrapper.like(User::getUsername, username);
            }
            if (Objects.nonNull(email)) {
                queryWrapper.like(User::getEmail, email);
            }

            List<User> userList = userMapper.selectList(queryWrapper);
            return userList.stream().map(user -> user.asViewObject(RoleUserVO.class)).toList();
        }
        return null;
    }

    /**
     * 给角色授权用户
     * @param userRoleDTO 用户角色对象
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> addUserRole(UserRoleDTO userRoleDTO) {
        List<Long> userIds = userRoleDTO.getUserId();
        Long roleId = userRoleDTO.getRoleId();
        // 避免重复添加
        List<Long> notUserIds = new ArrayList<>();
        LambdaQueryWrapper<UserRole> countWrapper = new LambdaQueryWrapper<>();
        userIds.forEach(userId -> {
            if (userRoleMapper.selectCount(countWrapper.eq(UserRole::getUserId, userId).eq(UserRole::getRoleId, roleId)) == 0) {
                notUserIds.add(userId);
            }
            countWrapper.clear();
        });
        List<UserRole> userRoles = notUserIds.stream().map(userId -> UserRole.builder().userId(userId).roleId(roleId).build()).toList();
        if (this.saveBatch(userRoles)) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 取消授权
     * @param userRoleDTO 用户角色对象
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> deleteUserRole(UserRoleDTO userRoleDTO) {
        int isDelete = userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, userRoleDTO.getRoleId()).in(UserRole::getUserId, userRoleDTO.getUserId()));
        if (isDelete > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 所有使用该用户的角色
     *
     * @param userId 用户id
     * @param roleName 角色名称
     * @param roleKey 角色字符
     * @param type 角色类型,0：该角色的使用用户 1：该角色的未使用用户
     * @return 角色列表
     */
    @Override
    public List<RoleAllVO> selectRoleByUserId(Long userId, String roleName, String roleKey, Integer type) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<Long> roleIds;
        if (Objects.equals(type, SelUserRoleEnum.HAS_USER.getType())) {
            // 查询存在用户的角色id
            roleIds = userRoleMapper.selectList(wrapper).stream().map(UserRole::getRoleId).toList();
        } else {
            // 查询存在用户的角色id
            roleIds = userRoleMapper.selectList(wrapper).stream().map(UserRole::getRoleId).toList();
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
     * 给多个角色添加某个用户
     * @param roleUserDTO 角色用户数据
     * @return 是否成功
     */
    @Transactional
    @Override
    public ResponseResult<Void> addRoleUser(RoleUserDTO roleUserDTO) {
        List<Long> roleIds = roleUserDTO.getRoleId();
        List<Long> userIds = roleUserDTO.getUserId();
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRole::getUserId, userIds)
                .in(UserRole::getRoleId, roleIds);
        // 如有，先删除
        if (userRoleMapper.selectCount(wrapper) > 0) userRoleMapper.delete(wrapper);

        List<UserRole> userRoles = new ArrayList<>();
        roleIds.forEach(roleId -> {
            userIds.forEach(userId -> {
                userRoles.add(UserRole.builder().roleId(roleId).userId(userId).build());
            });
        });
        if (saveBatch(userRoles)) return ResponseResult.success();

        return ResponseResult.failure();
    }

    /**
     * 批量或单个取消授权
     * @param roleUserDTO 角色用户数据
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> deleteRoleUser(RoleUserDTO roleUserDTO) {
        int isDelete = userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, roleUserDTO.getUserId()).in(UserRole::getRoleId, roleUserDTO.getRoleId()));
        if (isDelete > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }
}
