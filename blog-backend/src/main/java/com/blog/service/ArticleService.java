package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.ArticleDTO;
import com.blog.domain.dto.SearchArticleDTO;
import com.blog.domain.vo.*;
import com.blog.domain.entity.Article;
import com.blog.domain.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文章服务接口
 * <p>
 * 提供文章相关的业务逻辑处理，包括文章的增删改查、搜索、统计等功能。
 *
 * @author haibara
 * @description 文章服务接口
 * @since 2025/7/27
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询所有文章列表
     *
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 分页文章列表
     */
    PageVO<List<ArticleVO>> listAllArticle(Integer pageNum, Integer pageSize);

    /**
     * 查询推荐文章列表
     *
     * @return 推荐文章列表
     */
    List<RecommendArticleVO> listRecommendArticle();

    /**
     * 获取随机文章列表
     *
     * @return 随机文章列表
     */
    List<RandomArticleVO> listRandomArticle();

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情信息
     */
    ArticleDetailVO getArticleDetail(Integer id);

    /**
     * 获取相关文章列表
     *
     * @param categoryId 文章分类ID
     * @param articleId 当前文章ID
     * @return 相关文章列表
     */
    List<RelatedArticleVO> relatedArticleList(Integer categoryId, Integer articleId);

    /**
     * 获取时间轴数据
     *
     * @return 时间轴数据列表
     */
    List<TimeLineVO> listTimeLine();

    /**
     * 查询分类或标签下的文章
     *
     * @param type 类型（分类或标签）
     * @param typeId 类型ID
     * @return 文章列表
     */
    List<CategoryArticleVO> listCategoryArticle(Integer type, Long typeId);

    /**
     * 增加文章访问量
     *
     * @param id 文章ID
     */
    void addVisitCount(Long id);

    /**
     * 上传文章封面
     *
     * @param articleCover 文章封面文件
     * @return 封面图片URL
     */
    ResponseResult<String> uploadArticleCover(MultipartFile articleCover);

    /**
     * 发布文章
     *
     * @param articleDTO 文章数据传输对象
     * @return 操作结果
     */
    ResponseResult<Void> publish(ArticleDTO articleDTO);

    /**
     * 删除文章封面
     *
     * @param articleCoverUrl 文章封面URL
     * @return 操作结果
     */
    ResponseResult<Void> deleteArticleCover(String articleCoverUrl);

    /**
     * 上传文章图片
     *
     * @param articleImage 文章图片文件
     * @return 图片URL
     */
    ResponseResult<String> uploadArticleImage(MultipartFile articleImage);

    /**
     * 获取后台文章列表
     *
     * @return 文章列表
     */
    List<ArticleListVO> listArticle();

    /**
     * 搜索文章列表
     *
     * @param searchArticleDTO 搜索条件
     * @return 搜索结果
     */
    List<ArticleListVO> searchArticle(SearchArticleDTO searchArticleDTO);

    /**
     * 修改文章状态
     *
     * @param id 文章ID
     * @param status 文章状态
     * @return 操作结果
     */
    ResponseResult<Void> updateStatus(Long id, Integer status);

    /**
     * 修改文章置顶状态
     *
     * @param id 文章ID
     * @param isTop 是否置顶
     * @return 操作结果
     */
    ResponseResult<Void> updateIsTop(Long id, Integer isTop);

    /**
     * 获取文章编辑数据
     *
     * @param id 文章ID
     * @return 文章数据传输对象
     */
    ArticleDTO getArticleDTO(Long id);

    /**
     * 批量删除文章
     *
     * @param ids 文章ID列表
     * @return 操作结果
     */
    ResponseResult<Void> deleteArticle(List<Long> ids);

    /**
     * 初始化文章标题搜索数据
     *
     * @return 文章标题列表
     */
    List<InitSearchTitleVO> initSearchByTitle();

    /**
     * 获取热门文章列表
     *
     * @return 热门文章列表
     */
    List<HotArticleVO> listHotArticle();

    /**
     * 根据内容搜索文章
     *
     * @param content 搜索内容
     * @return 搜索结果
     */
    List<SearchArticleByContentVO> searchArticleByContent(String content);
}
