package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.ErrorConst;
import com.blog.constants.RedisConst;
import com.blog.constants.RegexConstant;
import com.blog.constants.SQLConst;
import com.blog.domain.dto.ArticleDTO;
import com.blog.domain.dto.SearchArticleDTO;
import com.blog.domain.entity.*;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.*;
import com.blog.enums.*;
import com.blog.exceptions.FileUploadException;
import com.blog.mapper.*;
import com.blog.service.*;
import com.blog.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 * <p>
 * 实现文章相关的业务逻辑处理，包括文章的增删改查、搜索、统计等功能。
 *
 * @author haibara
 * @description 文章服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private FavoriteService favoriteService;

    @Resource
    private LikeService likeService;

    @Resource
    private CommentService commentService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private FileUploadUtils fileUploadUtils;

    @Resource
    private UserMapper userMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ArticleTagService articleTagService;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 分页查询所有文章列表
     *
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 分页文章列表
     */
    @Override
    public PageVO<List<ArticleVO>> listAllArticle(Integer pageNum, Integer pageSize) {
        boolean hasKey = redisCache.isHasKey(RedisConst.ARTICLE_COMMENT_COUNT)
                && redisCache.isHasKey(RedisConst.ARTICLE_FAVORITE_COUNT)
                && redisCache.isHasKey(RedisConst.ARTICLE_LIKE_COUNT);
        // 文章
        Page<Article> page = new Page<>(pageNum, pageSize);
        this.page(page, new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, SQLConst.PUBLIC_ARTICLE)
                .orderByDesc(Article::getCreateTime));
        List<Article> list = page.getRecords();
        // 文章分类
        // 1. 优化：使用 Map 存储分类和标签信息，避免 N+1 问题
        Map<Long, String> categoryMap = categoryMapper
                .selectBatchIds(list.stream().map(Article::getCategoryId).toList())
                .stream().collect(Collectors.toMap(Category::getId, Category::getCategoryName));

        List<ArticleTag> articleTags = articleTagMapper
                .selectBatchIds(list.stream().map(Article::getId).toList());

        Map<Long, String> tagMap = tagMapper
                .selectBatchIds(articleTags.stream().map(ArticleTag::getTagId).toList())
                .stream().collect(Collectors.toMap(Tag::getId, Tag::getTagName));

        List<ArticleVO> articleVOS = list.stream().map(article -> {
            ArticleVO articleVO = article.asViewObject(ArticleVO.class);
            // 2. 优化：使用 Map 获取分类和标签信息
            articleVO.setCategoryName(categoryMap.get(article.getCategoryId()));
            articleVO.setTags(articleTags.stream()
                    .filter(at -> Objects.equals(at.getArticleId(), article.getId()))
                    .map(at -> tagMap.get(at.getTagId()))
                    .toList());
            return articleVO;
        }).toList();

        if (hasKey) {
            articleVOS = articleVOS.stream().peek(articleVO -> {
                setArticleCount(articleVO, RedisConst.ARTICLE_FAVORITE_COUNT, CountTypeEnum.FAVORITE);
                setArticleCount(articleVO, RedisConst.ARTICLE_LIKE_COUNT, CountTypeEnum.LIKE);
                setArticleCount(articleVO, RedisConst.ARTICLE_COMMENT_COUNT, CountTypeEnum.COMMENT);
            }).toList();
        }

        return new PageVO<>(articleVOS, page.getTotal());
    }

    /**
     * 设置文章统计数据
     *
     * @param articleVO 文章视图对象
     * @param redisKey Redis缓存键
     * @param articleFieldName 统计字段类型
     */
    private void setArticleCount(ArticleVO articleVO, String redisKey, CountTypeEnum articleFieldName) {
        String articleId = articleVO.getId().toString();
        Object countObj = redisCache.getCacheMap(redisKey).get(articleId);
        long count = 0L;
        if (countObj != null) {
            count = Long.parseLong(countObj.toString());
        } else {
            // 缓存发布新文章时数量缓存不存在
            redisCache.setCacheMap(redisKey, Map.of(articleId, 0));
        }

        if (articleFieldName.equals(CountTypeEnum.FAVORITE)) {
            articleVO.setFavoriteCount(count);
        } else if (articleFieldName.equals(CountTypeEnum.LIKE)) {
            articleVO.setLikeCount(count);
        } else if (articleFieldName.equals(CountTypeEnum.COMMENT)) {
            articleVO.setCommentCount(count);
        }
    }

    /**
     * 查询推荐文章列表
     *
     * @return 推荐文章列表
     */
    @Override
    public List<RecommendArticleVO> listRecommendArticle() {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getIsTop, SQLConst.RECOMMEND_ARTICLE)
                .and(i -> i.eq(Article::getStatus, SQLConst.PUBLIC_ARTICLE));
        List<Article> articles = articleMapper.selectList(wrapper);
        return articles
                .stream()
                .map(article -> article.asViewObject(RecommendArticleVO.class))
                .toList();
    }

    /**
     * 获取随机文章列表
     *
     * @return 随机文章列表
     */
    @Override
    public List<RandomArticleVO> listRandomArticle() {
        List<Article> randomArticles = articleMapper
                .selectRandomArticles(SQLConst.PUBLIC_ARTICLE, SQLConst.RANDOM_ARTICLE_COUNT);
        return randomArticles
                .stream()
                .map(article -> article.asViewObject(RandomArticleVO.class))
                .toList();
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情信息
     */
    @Override
    public ArticleDetailVO getArticleDetail(Integer id) {
        Article article = articleMapper
                .selectOne(new LambdaQueryWrapper<Article>()
                                .eq(Article::getStatus, SQLConst.PUBLIC_ARTICLE)
                                .and(i -> i.eq(Article::getId, id)));
        if (StringUtils.isNull(article)) return null;
        // 文章分类
        Category category = categoryMapper.selectById(article.getCategoryId());
        // 文章关系
        List<ArticleTag> articleTags = articleTagMapper
                .selectList(new LambdaQueryWrapper<ArticleTag>()
                        .eq(ArticleTag::getArticleId, article.getId()));
        // 标签
        List<Tag> tags = tagMapper
                .selectBatchIds(articleTags.stream().map(ArticleTag::getTagId).toList());
        // 当前文章的上一篇文章与下一篇文章,大于当前文章的最小文章与小于当前文章的最大文章
        LambdaQueryWrapper<Article> preAndNextWrapper = new LambdaQueryWrapper<>();
        preAndNextWrapper.lt(Article::getId, id);
        Article preArticle = articleMapper
                .selectOne(preAndNextWrapper.orderByDesc(Article::getId).last(SQLConst.LIMIT_ONE_SQL));

        preAndNextWrapper.clear();
        preAndNextWrapper.gt(Article::getId, id);
        Article nextArticle = articleMapper
                .selectOne(preAndNextWrapper.orderByAsc(Article::getId).last(SQLConst.LIMIT_ONE_SQL));

        return article.asViewObject(ArticleDetailVO.class, vo -> {
            vo.setCategoryName(category.getCategoryName());
            vo.setCategoryId(category.getId());
            vo.setTags(tags.stream().map(tag -> tag.asViewObject(TagVO.class)).toList());
            vo.setCommentCount(commentService
                    .count(new LambdaQueryWrapper<Comment>()
                            .eq(Comment::getTypeId, article.getId())
                            .eq(Comment::getType, CommentEnum.COMMENT_TYPE_ARTICLE.getType())));
            vo.setLikeCount(likeService
                    .count(new LambdaQueryWrapper<Like>()
                            .eq(Like::getTypeId, article.getId())
                            .eq(Like::getType, LikeEnum.LIKE_TYPE_ARTICLE.getType())));
            vo.setFavoriteCount(favoriteService
                    .count(new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getTypeId, article.getId())
                            .eq(Favorite::getType, FavoriteEnum.FAVORITE_TYPE_ARTICLE.getType())));
            vo.setPreArticleId(preArticle == null ? 0 : preArticle.getId());
            vo.setPreArticleTitle(preArticle == null ? "" : preArticle.getArticleTitle());
            vo.setNextArticleId(nextArticle == null ? 0 : nextArticle.getId());
            vo.setNextArticleTitle(nextArticle == null ? "" : nextArticle.getArticleTitle());
        });
    }

    /**
     * 获取相关文章列表
     *
     * @param categoryId 文章分类ID
     * @param articleId 当前文章ID
     * @return 相关文章列表
     */
    @Override
    public List<RelatedArticleVO> relatedArticleList(Integer categoryId, Integer articleId) {
        // 文章id不等于当前文章id,相关推荐排除自己，5条
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, SQLConst.PUBLIC_ARTICLE)
                        .and(i -> i.eq(Article::getCategoryId, categoryId))
                        .ne(Article::getId, articleId)
        );
        List<Article> articlesLimit5 = articles
                .stream()
                .limit(SQLConst.RELATED_ARTICLE_COUNT)
                .toList();
        return articlesLimit5
                .stream()
                .map(article -> article.asViewObject(RelatedArticleVO.class))
                .toList();
    }

    /**
     * 获取时间轴数据
     *
     * @return 时间轴数据列表
     */
    @Override
    public List<TimeLineVO> listTimeLine() {
        List<Article> list = this.query().list();
        return list
                .stream()
                .map(article -> article.asViewObject(TimeLineVO.class))
                .toList();
    }

    /**
     * 查询分类或标签下的文章
     *
     * @param type 类型（分类或标签）
     * @param typeId 类型ID
     * @return 文章列表
     */
    @Override
    public List<CategoryArticleVO> listCategoryArticle(Integer type, Long typeId) {
        List<Article> articles;
        if (Objects.equals(type, ListArticleEnum.BY_CATEGORY.getType()))
            articles = articleMapper
                    .selectList(new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, typeId));
        else if (Objects.equals(type, ListArticleEnum.BY_TAG.getType())) {
            List<Long> articleIds = articleTagMapper
                    .selectList(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, typeId))
                    .stream()
                    .map(ArticleTag::getArticleId)
                    .toList();
            if (!articleIds.isEmpty()) articles = articleMapper.selectBatchIds(articleIds);
            else articles = List.of();
        } else articles = List.of();

        if (Objects.isNull(articles) || articles.isEmpty()) return null;
        List<ArticleTag> articleTags = articleTagMapper
                .selectBatchIds(articles.stream().map(Article::getId).toList());
        List<Tag> tags = tagMapper
                .selectBatchIds(articleTags.stream().map(ArticleTag::getTagId).toList());

        return articles
                .stream()
                .map(article -> article.asViewObject(CategoryArticleVO.class, item -> {
            item.setCategoryId(articles
                    .stream()
                    .filter(art -> Objects.equals(art.getId(), article.getId()))
                    .findFirst().orElseThrow().getCategoryId());
            item.setTags(tags
                    .stream()
                    .filter(tag -> articleTags
                            .stream()
                            .anyMatch(articleTag -> Objects.equals(articleTag.getArticleId(), article.getId())
                                    && Objects.equals(articleTag.getTagId(), tag.getId()))
                    )
                    .map(tag -> tag.asViewObject(TagVO.class))
                    .toList());
        })).toList();
    }

    /**
     * 增加文章访问量
     *
     * @param id 文章ID
     */
    @Override
    public void addVisitCount(Long id) {
        // 访问量去重
        HttpServletRequest request = SecurityUtils.getCurrentHttpRequest();
        // key + id + ip + time(秒)
        String KEY = RedisConst.ARTICLE_VISIT_COUNT_LIMIT + id + ":" + IpUtils.getIpAddr(request);
        if (redisCache.getCacheObject(KEY) == null) {
            // 设置间隔时间
            redisCache.setCacheObject(KEY, 1, RedisConst.ARTICLE_VISIT_COUNT_INTERVAL, TimeUnit.SECONDS);

            if (redisCache.isHasKey(RedisConst.ARTICLE_VISIT_COUNT + id))
                redisCache.increment(RedisConst.ARTICLE_VISIT_COUNT + id, 1L);
            else redisCache.setCacheObject(RedisConst.ARTICLE_VISIT_COUNT + id, 0);
        }
    }

    /**
     * 上传文章封面
     *
     * @param articleCover 文章封面文件
     * @return 封面图片URL
     */
    @Override
    public ResponseResult<String> uploadArticleCover(MultipartFile articleCover) {
        try {
            String articleCoverUrl = null;
            try {
                articleCoverUrl = fileUploadUtils.upload(UploadEnum.ARTICLE_COVER, articleCover);
            } catch (FileUploadException e) {
                return ResponseResult.failure(e.getMessage());
            }
            if (StringUtils.isNotNull(articleCoverUrl))
                return ResponseResult.success(articleCoverUrl);
            else
                return ResponseResult.failure(ErrorConst.IMAGE_FORMAT_ERROR);
        } catch (Exception e) {
            log.error(ErrorConst.UPLOAD_IMAGE_FAILED, e);
            return ResponseResult.failure();
        }
    }

    /**
     * 发布文章
     *
     * @param articleDTO 文章数据传输对象
     * @return 操作结果
     */
    @Transactional
    @Override
    public ResponseResult<Void> publish(ArticleDTO articleDTO) {
        Article article = articleDTO.asViewObject(Article.class, v -> v.setUserId(SecurityUtils.getUserId()));
        if (this.saveOrUpdate(article)) {
            // 清除标签关系
            articleTagMapper.deleteById(article.getId());
            // 新增标签关系
            List<ArticleTag> articleTags = articleDTO
                    .getTagId()
                    .stream()
                    .map(articleTag -> ArticleTag
                            .builder()
                            .articleId(article.getId())
                            .tagId(articleTag)
                            .build())
                    .toList();
            articleTagService.saveBatch(articleTags);
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 删除文章封面
     *
     * @param articleCoverUrl 文章封面URL
     * @return 操作结果
     */
    @Override
    public ResponseResult<Void> deleteArticleCover(String articleCoverUrl) {
        try {
            // 提取图片名称
            String articleCoverName = articleCoverUrl
                    .substring(articleCoverUrl.indexOf(bucketName) + bucketName.length());
            fileUploadUtils.deleteFiles(List.of(articleCoverName));
            return ResponseResult.success();
        } catch (Exception e) {
            log.error(ErrorConst.DELETE_IMAGE_FAILED, e);
            return ResponseResult.failure();
        }
    }

    /**
     * 上传文章图片
     *
     * @param articleImage 文章图片文件
     * @return 图片URL
     */
    @Override
    public ResponseResult<String> uploadArticleImage(MultipartFile articleImage) {
        try {
            String url = fileUploadUtils.upload(UploadEnum.ARTICLE_IMAGE, articleImage);
            if (StringUtils.isNotNull(url))
                return ResponseResult.success(url);
            else
                return ResponseResult.failure(ErrorConst.IMAGE_FORMAT_ERROR);
        } catch (Exception e) {
            log.error(ErrorConst.UPLOAD_IMAGE_FAILED, e);
        }
        return null;
    }

    /**
     * 获取后台文章列表
     *
     * @return 文章列表
     */
    @Override
    public List<ArticleListVO> listArticle() {
        List<ArticleListVO> articleListVOS = articleMapper
                .selectList(new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreateTime))
                .stream()
                .map(article -> article.asViewObject(ArticleListVO.class))
                .toList();
        if (!articleListVOS.isEmpty()) {
            // 批量查询分类信息
            Set<Long> categoryIds = articleListVOS.stream().map(ArticleListVO::getCategoryId).collect(Collectors.toSet());
            Map<Long, String> categoryMap = categoryMapper.selectBatchIds(categoryIds)
                    .stream()
                    .collect(Collectors.toMap(Category::getId, Category::getCategoryName));

            // 批量查询用户信息
            Set<Long> userIds = articleListVOS.stream().map(ArticleListVO::getUserId).collect(Collectors.toSet());
            Map<Long, String> userMap = userMapper.selectBatchIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));

            // 批量查询文章标签关系
            List<Long> articleIds = articleListVOS.stream().map(ArticleListVO::getId).toList();
            List<ArticleTag> articleTags = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, articleIds));

            // 批量查询标签信息
            Set<Long> tagIds = articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toSet());
            Map<Long, String> tagMap = tagIds.isEmpty() ? Map.of() :
                    tagMapper.selectBatchIds(tagIds)
                            .stream()
                            .collect(Collectors.toMap(Tag::getId, Tag::getTagName));

            // 组装文章标签映射
            Map<Long, List<String>> articleTagMap = articleTags.stream()
                    .collect(Collectors.groupingBy(
                            ArticleTag::getArticleId,
                            Collectors.mapping(at -> tagMap.get(at.getTagId()), Collectors.toList())
                    ));

            // 设置关联数据
            articleListVOS.forEach(articleListVO -> {
                articleListVO.setCategoryName(categoryMap.get(articleListVO.getCategoryId()));
                articleListVO.setUserName(userMap.get(articleListVO.getUserId()));
                articleListVO.setTagsName(articleTagMap.getOrDefault(articleListVO.getId(), List.of()));
            });
            return articleListVOS;
        }
        return null;
    }

    /**
     * 搜索文章列表
     *
     * @param searchArticleDTO 搜索条件
     * @return 搜索结果
     */
    @Override
    public List<ArticleListVO> searchArticle(SearchArticleDTO searchArticleDTO) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotNull(searchArticleDTO.getArticleTitle()),
                        Article::getArticleTitle, searchArticleDTO.getArticleTitle())
                .eq(StringUtils.isNotNull(searchArticleDTO.getCategoryId()),
                        Article::getCategoryId, searchArticleDTO.getCategoryId())
                .eq(StringUtils.isNotNull(searchArticleDTO.getStatus()),
                        Article::getStatus, searchArticleDTO.getStatus())
                .eq(StringUtils.isNotNull(searchArticleDTO.getIsTop()),
                        Article::getIsTop, searchArticleDTO.getIsTop());
        List<ArticleListVO> articleListVOS = articleMapper
                .selectList(wrapper)
                .stream()
                .map(article -> article.asViewObject(ArticleListVO.class))
                .toList();
        if (!articleListVOS.isEmpty()) {
            // 批量查询分类信息
            Set<Long> categoryIds = articleListVOS.stream().map(ArticleListVO::getCategoryId).collect(Collectors.toSet());
            Map<Long, String> categoryMap = categoryMapper.selectBatchIds(categoryIds)
                    .stream()
                    .collect(Collectors.toMap(Category::getId, Category::getCategoryName));

            // 批量查询用户信息
            Set<Long> userIds = articleListVOS.stream().map(ArticleListVO::getUserId).collect(Collectors.toSet());
            Map<Long, String> userMap = userMapper.selectBatchIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));

            // 批量查询文章标签关系
            List<Long> articleIds = articleListVOS.stream().map(ArticleListVO::getId).toList();
            List<ArticleTag> articleTags = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, articleIds));

            // 批量查询标签信息
            Set<Long> tagIds = articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toSet());
            Map<Long, String> tagMap = tagIds.isEmpty() ? Map.of() :
                    tagMapper.selectBatchIds(tagIds)
                            .stream()
                            .collect(Collectors.toMap(Tag::getId, Tag::getTagName));

            // 组装文章标签映射
            Map<Long, List<String>> articleTagMap = articleTags.stream()
                    .collect(Collectors.groupingBy(
                            ArticleTag::getArticleId,
                            Collectors.mapping(at -> tagMap.get(at.getTagId()), Collectors.toList())
                    ));

            // 设置关联数据
            articleListVOS.forEach(articleListVO -> {
                articleListVO.setCategoryName(categoryMap.get(articleListVO.getCategoryId()));
                articleListVO.setUserName(userMap.get(articleListVO.getUserId()));
                articleListVO.setTagsName(articleTagMap.getOrDefault(articleListVO.getId(), List.of()));
            });
            return articleListVOS;
        }
        return null;
    }

    /**
     * 修改文章状态
     *
     * @param id 文章ID
     * @param status 文章状态
     * @return 操作结果
     */
    @Override
    public ResponseResult<Void> updateStatus(Long id, Integer status) {
        if (this.update(new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, status))) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 修改文章置顶状态
     *
     * @param id 文章ID
     * @param isTop 是否置顶
     * @return 操作结果
     */
    @Override
    public ResponseResult<Void> updateIsTop(Long id, Integer isTop) {
        if (this.update(new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getIsTop, isTop))) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 获取文章编辑数据
     *
     * @param id 文章ID
     * @return 文章数据传输对象
     */
    @Override
    public ArticleDTO getArticleDTO(Long id) {
        ArticleDTO articleDTO = articleMapper
                .selectById(id)
                .asViewObject(ArticleDTO.class);
        if (StringUtils.isNotNull(articleDTO)) {
            // 查询文章标签
            List<Long> tagIds = articleTagMapper
                    .selectList(new LambdaQueryWrapper<ArticleTag>()
                            .eq(ArticleTag::getArticleId, articleDTO.getId()))
                    .stream()
                    .map(ArticleTag::getTagId)
                    .toList();
            articleDTO.setTagId(tagMapper
                    .selectBatchIds(tagIds)
                    .stream()
                    .map(Tag::getId)
                    .toList());
            return articleDTO;
        }
        return null;
    }

    /**
     * 批量删除文章
     *
     * @param ids 文章ID列表
     * @return 操作结果
     */
    @Transactional
    @Override
    public ResponseResult<Void> deleteArticle(List<Long> ids) {
        if (this.removeByIds(ids)) {
            // 删除标签关系
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                    .in(ArticleTag::getArticleId, ids));
            // 删除点赞、收藏、评论
            likeMapper.delete(new LambdaQueryWrapper<Like>()
                    .eq(Like::getType, LikeEnum.LIKE_TYPE_ARTICLE.getType())
                    .and(a -> a.in(Like::getTypeId, ids)));
            favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getType, FavoriteEnum.FAVORITE_TYPE_ARTICLE.getType())
                    .and(a -> a.in(Favorite::getTypeId, ids)));
            commentMapper.delete(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getType, CommentEnum.COMMENT_TYPE_ARTICLE.getType())
                    .and(a -> a.in(Comment::getTypeId, ids)));
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 初始化文章标题搜索数据
     *
     * @return 文章标题列表
     */
    @Override
    public List<InitSearchTitleVO> initSearchByTitle() {
        List<Article> articles = this.list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, SQLConst.PUBLIC_ARTICLE));
        Map<Long, String> categoryMap = categoryMapper
                .selectList(null)
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::getCategoryName));
        if (articles.isEmpty()) {
            return List.of();
        }
        return articles
                .stream()
                .map(article -> article
                        .asViewObject(InitSearchTitleVO.class, item -> item
                                .setCategoryName(categoryMap
                                        .get(article.getCategoryId()))))
                .toList();
    }

    /**
     * 获取热门文章列表
     *
     * @return 热门文章列表
     */
    @Override
    public List<HotArticleVO> listHotArticle() {
        List<Article> articles = articleMapper
                .selectList(new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, SQLConst.PUBLIC_ARTICLE)
                        .orderByDesc(Article::getVisitCount)
                        .last("LIMIT 5"));
        if (!articles.isEmpty()) {
            return articles
                    .stream()
                    .map(article -> article.asViewObject(HotArticleVO.class))
                    .toList();
        }
        return List.of();
    }

    /**
     * 根据内容搜索文章
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @Override
    public List<SearchArticleByContentVO> searchArticleByContent(String keyword) {
        List<Article> articles = articleMapper
                .selectList(new LambdaQueryWrapper<Article>()
                        .like(Article::getArticleContent, keyword)
                        .eq(Article::getStatus, SQLConst.PUBLIC_ARTICLE));
        Map<Long, String> categoryMap = categoryMapper
                .selectList(null)
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::getCategoryName));
        if (!articles.isEmpty()) {
            List<SearchArticleByContentVO> listVos = articles
                    .stream()
                    .map(article -> article
                            .asViewObject(SearchArticleByContentVO.class, vo -> {
                vo.setCategoryName(categoryMap.get(article.getCategoryId()));
            })).toList();
            int index = -1;
            for (SearchArticleByContentVO articleVo : listVos) {
                String content = articleVo.getArticleContent();
                index = content.toLowerCase().indexOf(keyword.toLowerCase());
                if (index != -1) {
                    int end = Math.min(content.length(), index + keyword.length() + 20);
                    String highlighted = keyword + content.substring(index + keyword.length(), end);
                    articleVo.setArticleContent(stripMarkdown(highlighted));
                }
            }
            if (index != -1) {
                return listVos;
            }
        }
        return List.of();
    }

    /**
     * 去掉markdown格式
     *
     * @param markdown markdown
     * @return txt
     */
    private String stripMarkdown(String markdown) {
        return markdown.replaceAll(RegexConstant.HEADER_PATTERN, "") // 去掉标题
                .replaceAll(RegexConstant.BOLD_PATTERN, "$1") // 去掉加粗
                .replaceAll(RegexConstant.ITALIC_PATTERN, "$1") // 去掉斜体
                .replaceAll(RegexConstant.INLINE_CODE_PATTERN, "$1") // 去掉行内代码
                .replaceAll(RegexConstant.STRIKETHROUGH_PATTERN, "$1") // 去掉删除线
                .replaceAll(RegexConstant.LINK_PATTERN, "$1") // 去掉链接
                .replaceAll(RegexConstant.IMAGE_PATTERN, "") // 去掉图片
                .replaceAll(RegexConstant.BLOCKQUOTE_PATTERN, "") // 去掉引用
                .replaceAll(RegexConstant.UNORDERED_LIST_PATTERN, "") // 去掉无序列表
                .replaceAll(RegexConstant.ORDERED_LIST_PATTERN, "") // 去掉有序列表
                .replaceAll(RegexConstant.NEWLINE_PATTERN, " "); // 去掉换行符
    }
}
