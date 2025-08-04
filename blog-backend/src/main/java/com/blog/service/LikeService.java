package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.entity.Like;
import com.blog.domain.response.ResponseResult;

import java.util.List;

/**
 * @author haibara
 * @description 点赞服务接口
 * @since 2025/7/27
 */
public interface LikeService extends IService<Like> {

    /**
     * 点赞文章
     *
     * @param type   点赞类型
     * @param typeId 点赞id
     * @return 点赞结果
     */
    ResponseResult<Void> userLike(Integer type, Integer typeId);

    /**
     * 取消点赞
     *
     * @param type   点赞类型
     * @param typeId 点赞id
     * @return 取消点赞结果
     */
    ResponseResult<Void> cancelLike(Integer type, Integer typeId);

    /**
     * 是否点赞
     *
     * @param type   点赞类型
     * @param typeId 点赞id
     * @return 是否点赞
     */
    ResponseResult<List<Like>> isLike(Integer type, Integer typeId);

    /**
     * 获取点赞数
     *
     * @param likeTypeComment 点赞类型
     * @param id              点赞id
     * @return 点赞数量
     */
    Long getLikeCount(Integer likeTypeComment, Long id);
}
