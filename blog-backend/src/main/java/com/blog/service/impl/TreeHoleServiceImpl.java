package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.SQLConst;
import com.blog.domain.dto.SearchTreeHoleDTO;
import com.blog.domain.dto.TreeHoleIsCheckDTO;
import com.blog.domain.entity.TreeHole;
import com.blog.domain.entity.User;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.TreeHoleListVO;
import com.blog.domain.vo.TreeHoleVO;
import com.blog.mapper.TreeHoleMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.TreeHoleService;
import com.blog.utils.SecurityUtils;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 树洞服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("treeHoleService")
public class TreeHoleServiceImpl extends ServiceImpl<TreeHoleMapper, TreeHole> implements TreeHoleService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private TreeHoleMapper treeHoleMapper;

    /**
     * 新增树洞
     *
     * @param content 树洞内容
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> addTreeHole(String content) {
        if (this.save(TreeHole.builder().userId(SecurityUtils.getUserId()).content(content).build())) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 查看树洞
     *
     * @return 树洞列表
     */
    @Override
    public List<TreeHoleVO> getTreeHole() {
        List<TreeHole> treeHoles = treeHoleMapper
                .selectList(new LambdaQueryWrapper<TreeHole>()
                        .eq(TreeHole::getIsCheck, SQLConst.IS_CHECK_YES));
        if (treeHoles.isEmpty()) return List.of();

        // 批量查询用户信息
        Set<Long> userIds = treeHoles.stream().map(TreeHole::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        return treeHoles.stream().map(treeHole -> treeHole.asViewObject(TreeHoleVO.class, treeHoleVO -> {
            User user = userMap.get(treeHole.getUserId());
            if (user != null) {
                treeHoleVO.setNickname(user.getUsername());
                treeHoleVO.setAvatar(user.getAvatar());
            }
        })).collect(Collectors.toList());
    }

    /**
     * 后台树洞列表
     *
     * @param searchDTO 搜索请求
     * @return 结果
     */
    @Override
    public List<TreeHoleListVO> getBackTreeHoleList(SearchTreeHoleDTO searchDTO) {
        LambdaQueryWrapper<TreeHole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotNull(searchDTO)) {
            // 搜索
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getUsername, searchDTO.getUserName()));
            if (!users.isEmpty())
                wrapper.in(StringUtils.isNotEmpty(searchDTO.getUserName()), TreeHole::getUserId, users.stream().map(User::getId).collect(Collectors.toList()));
            else
                wrapper.eq(StringUtils.isNotNull(searchDTO.getUserName()), TreeHole::getUserId, null);

            wrapper.eq(StringUtils.isNotNull(searchDTO.getIsCheck()), TreeHole::getIsCheck, searchDTO.getIsCheck());
            if (StringUtils.isNotNull(searchDTO.getStartTime()) && StringUtils.isNotNull(searchDTO.getEndTime()))
                wrapper.between(TreeHole::getCreateTime, searchDTO.getStartTime(), searchDTO.getEndTime());
        }
        List<TreeHole> treeHoles = treeHoleMapper.selectList(wrapper);
        if (!treeHoles.isEmpty()) {
            // 批量查询用户信息
            Set<Long> userIds = treeHoles.stream().map(TreeHole::getUserId).collect(Collectors.toSet());
            Map<Long, String> userMap = userMapper.selectBatchIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));

            return treeHoles.stream().map(treeHole -> treeHole.asViewObject(TreeHoleListVO.class,
                    v -> v.setUserName(userMap.get(treeHole.getUserId())))).toList();
        }
        return null;
    }

    /**
     * 是否通过树洞
     *
     * @param isCheckDTO 是否通过
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> isCheckTreeHole(TreeHoleIsCheckDTO isCheckDTO) {
        if (treeHoleMapper.updateById(TreeHole.builder().id(isCheckDTO.getId()).isCheck(isCheckDTO.getIsCheck()).build()) > 0)
            return ResponseResult.success();

        return ResponseResult.failure();
    }

    /**
     * 删除树洞
     *
     * @param ids id 列表
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> deleteTreeHole(List<Long> ids) {
        if (treeHoleMapper.deleteBatchIds(ids) > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }
}
