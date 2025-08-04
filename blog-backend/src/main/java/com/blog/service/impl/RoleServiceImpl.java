package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.ErrorConst;
import com.blog.domain.dto.RoleDTO;
import com.blog.domain.dto.RoleSearchDTO;
import com.blog.domain.entity.Role;
import com.blog.domain.entity.RoleMenu;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.RoleAllVO;
import com.blog.domain.vo.RoleByIdVO;
import com.blog.domain.vo.RoleVO;
import com.blog.enums.RoleEnum;
import com.blog.mapper.RoleMapper;
import com.blog.mapper.RoleMenuMapper;
import com.blog.service.RoleMenuService;
import com.blog.service.RoleService;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author haibara
 * @description 系统角色服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private RoleMenuService roleMenuService;

    /**
     * 查询所有正常状态的角色
     *
     * @return 角色列表
     */
    @Override
    public ResponseResult<List<RoleVO>> selectAll() {
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getStatus, RoleEnum.ROLE_STATUS_NORMAL.getValue()));
        List<RoleVO> vos = roles.stream().map(role -> role.asViewObject(RoleVO.class)).toList();
        if (!vos.isEmpty()) return ResponseResult.success(vos);
        return ResponseResult.failure();
    }

    /**
     * 根据搜索条件查询角色
     *
     * @param roleSearchDTO 搜索条件
     * @return 角色列表
     */
    @Override
    public List<RoleAllVO> selectRole(RoleSearchDTO roleSearchDTO) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(roleSearchDTO)){
            wrapper.like(Objects.nonNull(roleSearchDTO.getRoleName()), Role::getRoleName, roleSearchDTO.getRoleName())
                    .like(Objects.nonNull(roleSearchDTO.getRoleKey()), Role::getRoleKey, roleSearchDTO.getRoleKey())
                    .eq(Objects.nonNull(roleSearchDTO.getStatus()), Role::getStatus, roleSearchDTO.getStatus());
            if (roleSearchDTO.getCreateTimeStart() != null && roleSearchDTO.getCreateTimeEnd() != null) {
                wrapper.gt(Role::getCreateTime, roleSearchDTO.getCreateTimeStart()).and(a -> a.lt(Role::getCreateTime, roleSearchDTO.getCreateTimeEnd()));
            }
        }
        wrapper.orderByAsc(Role::getOrderNum);
        return roleMapper.selectList(wrapper).stream().map(role -> role.asViewObject(RoleAllVO.class)).toList();
    }

    /**
     * 更新角色状态
     *
     * @param id 角色ID
     * @param status 角色状态（0-正常，1-禁用）
     * @return 更新结果
     */
    @Override
    public ResponseResult<Void> updateStatus(Long id, Integer status) {
        if (roleMapper.updateById(new Role().setId(id).setStatus(status)) > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 根据ID查询角色详细信息
     *
     * @param id 角色ID
     * @return 角色详细信息，包含关联的菜单ID列表
     */
    @Override
    public ResponseResult<RoleByIdVO> selectRoleById(Long id) {
        List<Long> menuIds = roleMenuMapper
                .selectList(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, id))
                .stream().map(RoleMenu::getMenuId).toList();
        Role role = roleMapper.selectById(id);
        if (role != null) {
            RoleByIdVO vo = role.asViewObject(RoleByIdVO.class, v -> v.setMenuIds(menuIds));
            return ResponseResult.success(vo);
        }
        return ResponseResult.failure();
    }

    /**
     * 更新或插入角色信息
     *
     * @param roleDTO 角色信息数据传输对象
     * @return 操作结果
     */
    @Transactional
    @Override
    public ResponseResult<Void> updateOrInsertRole(RoleDTO roleDTO) {
        Role role = roleDTO.asViewObject(Role.class);
        // 角色字符是否重复
        Role isRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, role.getRoleKey().trim()));
        if (StringUtils.isNotNull(isRole) && !isRole.getId().equals(role.getId())) {
            return ResponseResult.failure(ErrorConst.ROLE_KEY_CANNOT_BE_REPEAT);
        }
        if (this.saveOrUpdate(role)) {
            // 添加与菜单的权限
            roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, role.getId()));
            List<RoleMenu> roleMenus = roleDTO.getMenuIds().stream().map(menuId -> new RoleMenu(role.getId(), menuId)).toList();
            roleMenuService.saveBatch(roleMenus);
        }
        return ResponseResult.success();
    }

    /**
     * 删除角色
     *
     * @param ids 角色ID列表
     * @return 删除结果
     */
    @Transactional
    @Override
    public ResponseResult<Void> deleteRole(List<Long> ids) {
        if (roleMapper.deleteBatchIds(ids) > 0) {
            roleMenuMapper.deleteBatchIds(ids);
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }
}
