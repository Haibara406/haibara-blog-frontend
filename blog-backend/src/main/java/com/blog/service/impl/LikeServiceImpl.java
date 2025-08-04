package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.RedisConst;
import com.blog.domain.entity.Like;
import com.blog.domain.response.ResponseResult;
import com.blog.enums.LikeEnum;
import com.blog.mapper.LikeMapper;
import com.blog.service.LikeService;
import com.blog.utils.RedisCache;
import com.blog.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author haibara
 * @description 点赞服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("likeService")
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private RedisCache redisCache;


    /**
     * 点赞文章/留言/评论
     *
     * @param type   点赞类型
     * @param typeId 点赞id
     * @return 点赞结果
     */
    @Override
    public ResponseResult<Void> userLike(Integer type, Integer typeId) {
        // 查询是否已经点赞
        Like like = likeMapper.selectOne(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, SecurityUtils.getUserId())
                .eq(Like::getType, type)
                .eq(Like::getTypeId, typeId));
        if (like != null) return ResponseResult.failure();
        Like saveLike = Like.builder()
                .userId(SecurityUtils.getUserId())
                .type(type)
                .typeId(typeId).build();
        if (Objects.equals(type, LikeEnum.LIKE_TYPE_ARTICLE.getType()))
            redisCache.incrementCacheMapValue(RedisConst.ARTICLE_LIKE_COUNT, typeId.toString(), 1);
        if (this.save(saveLike)) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 取消点赞文章/留言/评论
     *
     * @param type   点赞类型
     * @param typeId 点赞id
     * @return 取消点赞结果
     */
    @Override
    public ResponseResult<Void> cancelLike(Integer type, Integer typeId) {
        // 查询是否已经点赞
        Like isLike = likeMapper.selectOne(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, SecurityUtils.getUserId())
                .eq(Like::getType, type)
                .eq(Like::getTypeId, typeId));
        if (Objects.isNull(isLike)) return ResponseResult.failure();
        boolean like = this.remove(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, SecurityUtils.getUserId())
                .eq(Like::getType, type)
                .eq(Like::getTypeId, typeId));
        if (Objects.equals(type, LikeEnum.LIKE_TYPE_ARTICLE.getType()))
            redisCache.incrementCacheMapValue(RedisConst.ARTICLE_LIKE_COUNT, typeId.toString(), -1);
        if (like) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 是否点赞文章/留言/评论
     *
     * @param type   点赞类型
     * @param typeId 点赞id
     * @return 是否点赞
     */
    @Override
    public ResponseResult<List<Like>> isLike(Integer type, Integer typeId) {
        // 未登录用户直接返回空列表，表示没有点赞
        if (!SecurityUtils.isLogin()) {
            return ResponseResult.success(Collections.emptyList());
        }

        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, SecurityUtils.getUserId())
                .eq(Like::getType, type);

        // 只有文章和留言点赞才会指定 typeId
        if (Objects.equals(type, LikeEnum.LIKE_TYPE_ARTICLE.getType()) ||
                Objects.equals(type, LikeEnum.LIKE_TYPE_LEAVE_WORD.getType())) {
            if (typeId != null) {
                wrapper.eq(Like::getTypeId, typeId);
            }
        }

        List<Like> likes = likeMapper.selectList(wrapper);
        return ResponseResult.success(likes);
    }

    /**
     * 获取点赞数
     *
     * @param likeTypeComment 点赞类型
     * @param id              点赞id
     * @return 点赞数量
     */
    @Override
    public Long getLikeCount(Integer likeTypeComment, Long id) {
        return likeMapper.selectCount(new LambdaQueryWrapper<Like>()
                .eq(Like::getType, likeTypeComment)
                .eq(Like::getTypeId, id));
    }
}
