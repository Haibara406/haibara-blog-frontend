package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.CommentIsCheckDTO;
import com.blog.domain.dto.SearchCommentDTO;
import com.blog.domain.dto.UserCommentDTO;
import com.blog.domain.entity.Comment;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.ArticleCommentVO;
import com.blog.domain.vo.CommentListVO;
import com.blog.domain.vo.PageVO;

import java.util.List;

/**
 * @author haibara
 * @description 评论服务接口
 * @since 2025/7/27
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询评论列表
     * @param type 评论类型
     * @param typeId 关联对象ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页评论列表
     */
    PageVO<List<ArticleCommentVO>> getComment(Integer type, Integer typeId, Integer pageNum, Integer pageSize);

    /**
     * 用户发布评论
     * @param commentDTO 评论数据
     * @return 发布结果
     */
    ResponseResult<String> userComment(UserCommentDTO commentDTO);

    /**
     * 获取后台评论列表
     * @param searchDTO 搜索条件
     * @return 评论列表
     */
    List<CommentListVO> getBackCommentList(SearchCommentDTO searchDTO);

    /**
     * 审核评论
     * @param isCheckDTO 审核状态数据
     * @return 操作结果
     */
    ResponseResult<Void> isCheckComment(CommentIsCheckDTO isCheckDTO);

    /**
     * 删除评论
     * @param id 评论ID
     * @return 操作结果
     */
    ResponseResult<Void> deleteComment(Long id);
}
