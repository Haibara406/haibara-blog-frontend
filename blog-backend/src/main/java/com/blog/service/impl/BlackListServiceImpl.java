package com.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.BlackListConst;
import com.blog.constants.ErrorConst;
import com.blog.constants.RedisConst;
import com.blog.domain.dto.AddBlackListDTO;
import com.blog.domain.dto.SearchBlackListDTO;
import com.blog.domain.dto.UpdateBlackListDTO;
import com.blog.domain.entity.BlackList;
import com.blog.domain.entity.User;
import com.blog.domain.ip.BlackListIpInfo;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.BlackListVO;
import com.blog.mapper.BlackListMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.BlackListService;
import com.blog.service.IpService;
import com.blog.utils.IpUtils;
import com.blog.utils.RedisCache;
import com.blog.utils.SecurityUtils;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 黑名单服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("blackListService")
public class BlackListServiceImpl extends ServiceImpl<BlackListMapper, BlackList> implements BlackListService {

    @Resource
    private BlackListMapper blackListMapper;

    @Resource
    private IpService ipService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisCache redisCache;


    /**
     * 新增黑名单数据
     *
     * @param addBlackListDTO 新增黑名单对象
     * @return 新增结果
     */
    @Override
    public ResponseResult<Void> addBlackList(AddBlackListDTO addBlackListDTO) {
        if (!addBlackListDTO.getUserIds().isEmpty()) {
            Long count = blackListMapper.selectCount(new LambdaQueryWrapper<BlackList>().in(BlackList::getUserId, addBlackListDTO.getUserIds()));
            if (count > 0) {
                return ResponseResult.failure(ErrorConst.USER_ALREADY_EXISTS_IN_BLACKLIST);
            }

            for (int i = 0; i < addBlackListDTO.getUserIds().size(); i++) {
                if (!saveBlackList(addBlackListDTO, i)) {
                    return ResponseResult.failure(ErrorConst.FAILED_TO_ADD_BLACKLIST);
                }
            }
        } else {
            if (!saveBlackList(addBlackListDTO, null)) {
                return ResponseResult.failure(ErrorConst.FAILED_TO_ADD_BLACKLIST);
            }
        }

        return ResponseResult.success();
    }

    /**
     * 保存黑名单数据
     *
     * @param addBlackListDTO 新增黑名单对象
     * @param index 用户ID索引
     * @return 保存结果
     */
    protected Boolean saveBlackList(AddBlackListDTO addBlackListDTO, Integer index) {
        BlackList blackList = BlackList.builder()
                .userId(!addBlackListDTO.getUserIds().isEmpty() ? addBlackListDTO.getUserIds().get(index) : null)
                .reason(addBlackListDTO.getReason())
                .type(!addBlackListDTO.getUserIds().isEmpty() ? BlackListConst.BLACK_LIST_TYPE_USER : BlackListConst.BLACK_LIST_TYPE_BOT)
                .expiresTime(addBlackListDTO.getExpiresTime()).build();

        BlackListIpInfo blackListIpInfo = BlackListIpInfo.builder()
                .createIp(!addBlackListDTO.getUserIds().isEmpty() ? null : IpUtils.getIpAddr(SecurityUtils.getCurrentHttpRequest()))
                .build();
        blackList.setIpInfo(blackListIpInfo);
        if (addBlackListDTO.getUserIds().isEmpty()) {
            Long idByIp = blackListMapper.getIdByIp(blackListIpInfo.getCreateIp());
            if (idByIp != null) {
                // 存在
                blackList.setId(idByIp);
            }
        }
        if (null != blackList.getId() ? this.updateById(blackList) : this.save(blackList)) {
            if (blackList.getType() == BlackListConst.BLACK_LIST_TYPE_BOT) {
                ipService.refreshIpDetailAsyncByBid(blackList.getId());
            }
            updateBlackListCache(blackList);
        } else {
            return false;
        }
        return true;
    }

    /**
     * 获取黑名单
     *
     * @param searchBlackListDTO 搜索条件
     * @return 黑名单列表
     */
    @Override
    public List<BlackListVO> getBlackList(SearchBlackListDTO searchBlackListDTO) {
        LambdaQueryWrapper<BlackList> queryWrapper = new LambdaQueryWrapper<>();

        if (searchBlackListDTO != null) {
            // 搜索
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getUsername, searchBlackListDTO.getUserName()));
            if (!users.isEmpty())
                queryWrapper.in(StringUtils.isNotEmpty(searchBlackListDTO.getUserName()), BlackList::getUserId, users.stream().map(User::getId).collect(Collectors.toList()));
            else
                queryWrapper.eq(StringUtils.isNotNull(searchBlackListDTO.getUserName()), BlackList::getUserId, null);

            queryWrapper.like(StrUtil.isNotBlank(searchBlackListDTO.getReason()), BlackList::getReason, searchBlackListDTO.getReason())
                    .eq(null != searchBlackListDTO.getType(), BlackList::getType, searchBlackListDTO.getType())
                    .between(StringUtils.isNotNull(searchBlackListDTO.getStartTime()) && StringUtils.isNotNull(searchBlackListDTO.getEndTime()), BlackList::getBannedTime, searchBlackListDTO.getStartTime(), searchBlackListDTO.getEndTime());
        }
        queryWrapper.orderByDesc(BlackList::getCreateTime);

        List<BlackList> blackLists = this.list(queryWrapper);
        if (blackLists.isEmpty()) {
            return List.of();
        }

        // 批量查询用户信息
        Set<Long> userIds = blackLists.stream()
                .map(BlackList::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> userMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(userIds)
                        .stream()
                        .collect(Collectors.toMap(User::getId, User::getUsername));

        return blackLists.stream()
                .map(blackList -> blackList.asViewObject(BlackListVO.class, black -> {
                    if (blackList.getUserId() != null) {
                        black.setUserName(userMap.get(blackList.getUserId()));
                    }
                }))
                .toList();
    }

    /**
     * 修改黑名单数据
     *
     * @param updateBlackListDTO 修改黑名单对象
     * @return 修改结果
     */
    @Override
    public ResponseResult<Void> updateBlackList(UpdateBlackListDTO updateBlackListDTO) {
        BlackList blackList = BlackList.builder()
                .id(updateBlackListDTO.getId())
                .reason(updateBlackListDTO.getReason())
                .expiresTime(updateBlackListDTO.getExpiresTime()).build();
        if (this.updateById(blackList)) {
            // 修改缓存
            BlackList black = blackListMapper.selectById(blackList.getId());
            updateBlackListCache(black);
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 更新黑名单缓存
     *
     * @param blackList 黑名单实体
     */
    private void updateBlackListCache(BlackList blackList) {
        if (blackList.getType() == BlackListConst.BLACK_LIST_TYPE_BOT) {
            // 更新redis缓存
            redisCache.setCacheMapValue(RedisConst.BLACK_LIST_IP_KEY, blackList.getIpInfo().getCreateIp(), blackList.getExpiresTime());
        } else if (blackList.getType() == BlackListConst.BLACK_LIST_TYPE_USER) {
            redisCache.setCacheMapValue(RedisConst.BLACK_LIST_UID_KEY, blackList.getUserId().toString(), blackList.getExpiresTime());
        }
    }

    /**
     * 删除黑名单
     *
     * @param ids 黑名单id列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<Void> deleteBlackList(List<Long> ids) {
        // 清除缓存
        blackListMapper.selectBatchIds(ids).forEach(blackList -> {
            if (blackList.getType() == BlackListConst.BLACK_LIST_TYPE_BOT) {
                // 清除缓存
                redisCache.deleteCacheMapValue(RedisConst.BLACK_LIST_IP_KEY, blackList.getIpInfo().getCreateIp());
            } else if (blackList.getType() == BlackListConst.BLACK_LIST_TYPE_USER) {
                redisCache.deleteCacheMapValue(RedisConst.BLACK_LIST_UID_KEY, blackList.getUserId().toString());
            }
        });
        if (this.removeBatchByIds(ids)) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }
}
