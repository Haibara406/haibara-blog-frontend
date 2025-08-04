package com.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.ErrorConst;
import com.blog.constants.FunctionConst;
import com.blog.constants.SQLConst;
import com.blog.domain.dto.LeaveWordIsCheckDTO;
import com.blog.domain.dto.SearchLeaveWordDTO;
import com.blog.domain.entity.*;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LeaveWordListVO;
import com.blog.domain.vo.LeaveWordVO;
import com.blog.enums.CommentEnum;
import com.blog.enums.FavoriteEnum;
import com.blog.enums.LikeEnum;
import com.blog.enums.MailboxAlertsEnum;
import com.blog.mapper.*;
import com.blog.service.LeaveWordService;
import com.blog.service.PublicService;
import com.blog.utils.SecurityUtils;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 留言服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("leaveWordService")
public class LeaveWordServiceImpl extends ServiceImpl<LeaveWordMapper, LeaveWord> implements LeaveWordService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private LeaveWordMapper leaveWordMapper;

    @Resource
    private PublicService publicService;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${mail.message-new-notice}")
    private Boolean messageNewNotice;


    /**
     * 查询留言板
     *
     * @param id 留言板id
     * @return 留言板列表
     */
    @Override
    public List<LeaveWordVO> getLeaveWordList(String id) {
        List<LeaveWord> leaveWords = this.query()
                .eq(SQLConst.IS_CHECK, SQLConst.IS_CHECK_YES)
                .eq(id != null, SQLConst.ID, id)
                .orderByDesc(SQLConst.CREATE_TIME)
                .list();

        if (leaveWords.isEmpty()) {
            return List.of();
        }

        // 批量查询用户信息
        Set<Long> userIds = leaveWords.stream().map(LeaveWord::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 批量查询评论数量
        List<Long> leaveWordIds = leaveWords.stream().map(LeaveWord::getId).toList();
        Map<Long, Long> commentCountMap = getCommentCountBatch(leaveWordIds);

        // 批量查询点赞数量
        Map<Long, Long> likeCountMap = getLikeCountBatch(leaveWordIds);

        // 批量查询收藏数量
        Map<Long, Long> favoriteCountMap = getFavoriteCountBatch(leaveWordIds);

        return leaveWords.stream().map(leaveWord -> {
            User user = userMap.get(leaveWord.getUserId());
            return leaveWord.asViewObject(LeaveWordVO.class, leaveWordVO -> {
                if (user != null) {
                    leaveWordVO.setNickname(user.getNickname()).setAvatar(user.getAvatar());
                }
                leaveWordVO.setCommentCount(commentCountMap.getOrDefault(leaveWord.getId(), 0L));
                leaveWordVO.setLikeCount(likeCountMap.getOrDefault(leaveWord.getId(), 0L));
                leaveWordVO.setFavoriteCount(favoriteCountMap.getOrDefault(leaveWord.getId(), 0L));
            });
        }).toList();
    }

    /**
     * 添加留言板
     *
     * @param content 留言内容
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> userLeaveWord(String content) {
        String parse = (String) JSON.parse(content);
        if (parse.length() > FunctionConst.LEAVE_WORD_CONTENT_LENGTH) {
            return ResponseResult.failure(ErrorConst.LEAVE_WORD_CONTENT_TOO_LONG);
        }
        LeaveWord build = LeaveWord.builder().content(parse)
                .userId(SecurityUtils.getUserId()).build();

        if (this.save(build)){
            // 是否是站长本人留言
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getId, SecurityUtils.getUserId()));
            if (Objects.equals(user.getEmail(), email) || !messageNewNotice) return ResponseResult.success();

            // 留言成功，发送邮箱提醒给站长
            Map<String, Object> map = new HashMap<>();
            map.put("messageId",build.getId());
            publicService.sendEmail(MailboxAlertsEnum.MESSAGE_NOTIFICATION_EMAIL.getCodeStr(), email, map);

            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 后台留言列表
     *
     * @param searchDTO 查询dto
     * @return 结果
     */
    @Override
    public List<LeaveWordListVO> getBackLeaveWordList(SearchLeaveWordDTO searchDTO) {
        LambdaQueryWrapper<LeaveWord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotNull(searchDTO)) {
            // 搜索
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getUsername, searchDTO.getUserName()));
            if (!users.isEmpty())
                wrapper.in(StringUtils.isNotEmpty(searchDTO.getUserName()), LeaveWord::getUserId, users.stream().map(User::getId).collect(Collectors.toList()));
            else
                wrapper.eq(StringUtils.isNotNull(searchDTO.getUserName()), LeaveWord::getUserId, null);

            wrapper.eq(StringUtils.isNotNull(searchDTO.getIsCheck()), LeaveWord::getIsCheck, searchDTO.getIsCheck());
            if (StringUtils.isNotNull(searchDTO.getStartTime()) && StringUtils.isNotNull(searchDTO.getEndTime()))
                wrapper.between(LeaveWord::getCreateTime, searchDTO.getStartTime(), searchDTO.getEndTime());
        }
        List<LeaveWord> leaveWords = leaveWordMapper.selectList(wrapper);
        if (!leaveWords.isEmpty()) {
            // 批量查询用户信息
            Set<Long> userIds = leaveWords.stream().map(LeaveWord::getUserId).collect(Collectors.toSet());
            Map<Long, String> userMap = userMapper.selectBatchIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));

            return leaveWords.stream().map(leaveWord -> leaveWord.asViewObject(LeaveWordListVO.class,
                    v -> v.setUserName(userMap.get(leaveWord.getUserId())))).toList();
        }
        return null;
    }

    /**
     * 是否通过留言
     *
     * @param isCheckDTO 是否通过
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> isCheckLeaveWord(LeaveWordIsCheckDTO isCheckDTO) {
        if (leaveWordMapper.updateById(LeaveWord.builder().id(isCheckDTO.getId()).isCheck(isCheckDTO.getIsCheck()).build()) > 0)
            return ResponseResult.success();

        return ResponseResult.failure();
    }

    /**
     * 删除留言
     *
     * @param ids id 列表
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> deleteLeaveWord(List<Long> ids) {
        if (leaveWordMapper.deleteBatchIds(ids) > 0) {
            // 删除点赞、收藏、评论
            likeMapper.delete(new LambdaQueryWrapper<Like>()
                    .eq(Like::getType, LikeEnum.LIKE_TYPE_LEAVE_WORD.getType())
                    .and(a -> a.in(Like::getTypeId, ids)));
            favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getType, FavoriteEnum.FAVORITE_TYPE_LEAVE_WORD.getType())
                    .and(a -> a.in(Favorite::getTypeId, ids)));
            commentMapper.delete(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getType, CommentEnum.COMMENT_TYPE_LEAVE_WORD.getType())
                    .and(a -> a.in(Comment::getTypeId, ids)));
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 批量查询评论数量
     */
    private Map<Long, Long> getCommentCountBatch(List<Long> leaveWordIds) {
        if (leaveWordIds.isEmpty()) return Map.of();

        return commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getType, CommentEnum.COMMENT_TYPE_LEAVE_WORD.getType())
                .eq(Comment::getIsCheck, SQLConst.IS_CHECK_YES)
                .in(Comment::getTypeId, leaveWordIds))
                .stream()
                .collect(Collectors.groupingBy(comment -> Long.valueOf(comment.getTypeId()), Collectors.counting()));
    }

    /**
     * 批量查询点赞数量
     */
    private Map<Long, Long> getLikeCountBatch(List<Long> leaveWordIds) {
        if (leaveWordIds.isEmpty()) return Map.of();

        return likeMapper.selectList(new LambdaQueryWrapper<Like>()
                .eq(Like::getType, LikeEnum.LIKE_TYPE_LEAVE_WORD.getType())
                .in(Like::getTypeId, leaveWordIds))
                .stream()
                .collect(Collectors.groupingBy(like -> Long.valueOf(like.getTypeId()), Collectors.counting()));
    }

    /**
     * 批量查询收藏数量
     */
    private Map<Long, Long> getFavoriteCountBatch(List<Long> leaveWordIds) {
        if (leaveWordIds.isEmpty()) return Map.of();

        return favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getType, FavoriteEnum.FAVORITE_TYPE_LEAVE_WORD.getType())
                .in(Favorite::getTypeId, leaveWordIds))
                .stream()
                .collect(Collectors.groupingBy(favorite -> Long.valueOf(favorite.getTypeId()), Collectors.counting()));
    }
}
