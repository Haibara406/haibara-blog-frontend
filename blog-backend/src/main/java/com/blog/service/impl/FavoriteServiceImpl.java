package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.RedisConst;
import com.blog.domain.dto.FavoriteIsCheckDTO;
import com.blog.domain.dto.SearchFavoriteDTO;
import com.blog.domain.entity.Article;
import com.blog.domain.entity.Favorite;
import com.blog.domain.entity.LeaveWord;
import com.blog.domain.entity.User;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.FavoriteListVO;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.FavoriteMapper;
import com.blog.mapper.LeaveWordMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.FavoriteService;
import com.blog.utils.RedisCache;
import com.blog.utils.SecurityUtils;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 收藏服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("favoriteService")
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private LeaveWordMapper leaveWordMapper;

    /**
     * 收藏文章
     *
     * @param type   收藏类型
     * @param typeId 收藏id
     * @return 收藏结果
     */
    @Override
    public ResponseResult<Void> userFavorite(Integer type, Long typeId) {
        // 查询是否已经收藏
        Favorite favorite = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, SecurityUtils.getUserId())
                .eq(Favorite::getType, type)
                .eq(Favorite::getTypeId, typeId));
        if (StringUtils.isNotNull(favorite)) return ResponseResult.failure();
        Favorite Savefavorite = Favorite.builder()
                .id(null)
                .userId(SecurityUtils.getUserId())
                .type(type)
                .typeId(typeId).build();
        redisCache.incrementCacheMapValue(RedisConst.ARTICLE_FAVORITE_COUNT, typeId.toString(), 1);
        if (this.save(Savefavorite)) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 取消收藏文章
     *
     * @param type   收藏类型
     * @param typeId 收藏id
     * @return 取消收藏结果
     */
    @Override
    public ResponseResult<Void> cancelFavorite(Integer type, Integer typeId) {
        // 查询是否已经收藏
        Favorite isFavorite = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, SecurityUtils.getUserId())
                .eq(Favorite::getType, type)
                .eq(Favorite::getTypeId, typeId));
        if (Objects.isNull(isFavorite)) return ResponseResult.failure();
        boolean cancelFavorite = this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, SecurityUtils.getUserId())
                .eq(Favorite::getType, type)
                .eq(Favorite::getTypeId, typeId));
        redisCache.incrementCacheMapValue(RedisConst.ARTICLE_FAVORITE_COUNT, typeId.toString(), -1);
        if (cancelFavorite) return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 是否已经收藏
     *
     * @param type   收藏类型
     * @param typeId 收藏id
     * @return 是否已经收藏
     */
    @Override
    public Boolean isFavorite(Integer type, Integer typeId) {
        if (SecurityUtils.isLogin()) {
            // 是否收藏
            Favorite favorite = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, SecurityUtils.getUserId())
                    .eq(Favorite::getType, type)
                    .eq(Favorite::getTypeId, typeId));
            return favorite != null;
        }
        return false;
    }

    /**
     * 后台收藏列表
     *
     * @param searchDTO
     * @return 结果
     */
    @Override
    public List<FavoriteListVO> getBackFavoriteList(SearchFavoriteDTO searchDTO) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotNull(searchDTO)) {
            // 搜索
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getUsername, searchDTO.getUserName()));
            if (!users.isEmpty())
                wrapper.in(StringUtils.isNotEmpty(searchDTO.getUserName()), Favorite::getUserId, users.stream().map(User::getId).collect(Collectors.toList()));
            else
                wrapper.eq(StringUtils.isNotNull(searchDTO.getUserName()), Favorite::getUserId, null);

            wrapper.eq(StringUtils.isNotNull(searchDTO.getIsCheck()), Favorite::getIsCheck, searchDTO.getIsCheck())
                    .eq(StringUtils.isNotNull(searchDTO.getType()), Favorite::getType, searchDTO.getType());
            if (StringUtils.isNotNull(searchDTO.getStartTime()) && StringUtils.isNotNull(searchDTO.getEndTime()))
                wrapper.between(Favorite::getCreateTime, searchDTO.getStartTime(), searchDTO.getEndTime());
        }
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        if (!favorites.isEmpty()) {
            // 批量查询用户信息
            Set<Long> userIds = favorites.stream().map(Favorite::getUserId).collect(Collectors.toSet());
            Map<Long, String> userMap = userMapper.selectBatchIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));

            // 批量查询文章内容
            List<Long> articleIds = favorites.stream()
                    .filter(f -> f.getType() == 1)
                    .map(Favorite::getTypeId)
                    .toList();
            Map<Long, String> articleMap = articleIds.isEmpty() ? Map.of() :
                    articleMapper.selectBatchIds(articleIds)
                            .stream()
                            .collect(Collectors.toMap(article -> article.getId(), article -> article.getArticleContent()));

            // 批量查询留言内容
            List<Long> leaveWordIds = favorites.stream()
                    .filter(f -> f.getType() == 2)
                    .map(Favorite::getTypeId)
                    .toList();
            Map<Long, String> leaveWordMap = leaveWordIds.isEmpty() ? Map.of() :
                    leaveWordMapper.selectBatchIds(leaveWordIds)
                            .stream()
                            .collect(Collectors.toMap(leaveWord -> leaveWord.getId(), leaveWord -> leaveWord.getContent()));

            return favorites.stream().map(favorite -> favorite.asViewObject(FavoriteListVO.class,
                    v -> {
                        v.setUserName(userMap.get(favorite.getUserId()));
                        switch (favorite.getType()) {
                            case 1 -> v.setContent(articleMap.get(favorite.getTypeId()));
                            case 2 -> v.setContent(leaveWordMap.get(favorite.getTypeId()));
                        }
                    })).toList();
        }
        return null;
    }

    /**
     * 是否通过收藏
     *
     * @param isCheckDTO 是否通过
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> isCheckFavorite(FavoriteIsCheckDTO isCheckDTO) {
        if (favoriteMapper.updateById(Favorite.builder().id(isCheckDTO.getId()).isCheck(isCheckDTO.getIsCheck()).build()) > 0)
            return ResponseResult.success();
        return ResponseResult.failure();
    }

    /**
     * 删除收藏
     *
     * @param ids id 列表
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> deleteFavorite(List<Long> ids) {
        if (favoriteMapper.deleteBatchIds(ids) > 0) return ResponseResult.success();
        return ResponseResult.failure();
    }
}
