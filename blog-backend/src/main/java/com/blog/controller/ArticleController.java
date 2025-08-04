package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.constants.ValidationConstants;
import com.blog.domain.dto.ArticleDTO;
import com.blog.domain.dto.SearchArticleDTO;
import com.blog.domain.vo.*;
import com.blog.domain.response.ResponseResult;
import com.blog.service.ArticleService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文章管理控制器
 *
 * @author haibara
 * @description 文章相关接口
 * @since 2025/7/29 16:53
 */

@RestController
@Tag(name = "文章相关接口")
@RequestMapping("/article")
@Validated
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 初始化标题搜索文章数据
     * <p>
     * 获取所有公开文章的标题和ID信息，用于前端搜索功能的初始化数据。
     * 该接口为前端搜索组件提供文章标题的自动补全和快速搜索功能。
     *
     * @return 响应结果，包含文章标题搜索初始化数据
     *         <ul>
     *             <li>成功时返回文章列表，每个文章包含ID和标题信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see InitSearchTitleVO 搜索标题初始化视图对象
     */
    @Operation(summary = "初始化通过标题搜索文章")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.SEARCH_ARTICLE_MAX_COUNT)
    @GetMapping("/search/init/title")
    public ResponseResult<List<InitSearchTitleVO>> initSearchByTitle() {
        return ControllerUtils.messageHandler(() -> articleService.initSearchByTitle());
    }

    /**
     * 根据内容搜索文章
     * <p>
     * 根据关键词在文章标题和内容中进行模糊搜索，返回匹配的文章列表。
     * 搜索关键词长度限制在1-15个字符之间，支持中英文搜索。
     * 该接口用于前端文章搜索功能。
     *
     * @param content 搜索关键词，必填参数，长度限制1-15个字符
     * @return 响应结果，包含匹配的文章列表
     *         <ul>
     *             <li>成功时返回文章列表，每个文章包含基本信息和匹配的内容片段</li>
     *             <li>失败时返回错误信息，可能是因为搜索关键词为空或长度超限</li>
     *         </ul>
     * @throws IllegalArgumentException 当搜索内容为空或长度不符合要求时抛出
     * @see SearchArticleByContentVO 内容搜索结果视图对象
     */
    @Operation(summary = "内容搜索文章")
    @Parameters({
            @Parameter(name = "content", description = "搜索文章内容", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.SEARCH_ARTICLE_MAX_COUNT)
    @GetMapping("/search/by/content")
    public ResponseResult<List<SearchArticleByContentVO>> searchByContent(
            @NotEmpty(message = ValidationConstants.ARTICLE_CONTENT_NOT_NULL)
            @Length(min = 1, max = 15, message = ValidationConstants.ARTICLE_CONTENT_SEARCH_TOO_LONG)
            @RequestParam("content") String content
    ) {
        return ControllerUtils.messageHandler(() -> articleService.searchArticleByContent(content));
    }

    /**
     * 获取热门推荐文章
     * <p>
     * 获取热门文章列表，通常基于文章的浏览量、点赞数、评论数等指标进行排序。
     * 该接口用于前端首页或侧边栏的热门文章推荐功能。
     *
     * @return 响应结果，包含热门文章列表
     *         <ul>
     *             <li>成功时返回热门文章列表，每个文章包含基本信息和热度指标</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see HotArticleVO 热门文章视图对象
     */
    @Operation(summary = "获取热门推荐文章")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_HOT_ARTICLE_MAX_COUNT)
    @GetMapping("/hot")
    public ResponseResult<List<HotArticleVO>> hot() {
        return ControllerUtils.messageHandler(() -> articleService.listHotArticle());
    }


    /**
     * 获取所有文章列表（分页）
     * <p>
     * 分页获取所有公开状态的文章列表，包含文章基本信息、分类、标签和统计数据。
     * 文章按创建时间倒序排列，支持自定义页码和页面大小。
     * 该接口用于前端博客首页和文章列表页面。
     *
     * @param pageNum 页码，必填参数，从1开始
     * @param pageSize 每页数量，必填参数，控制每页显示的文章数量
     * @return 响应结果，包含分页的文章列表
     *         <ul>
     *             <li>成功时返回文章分页数据，每个文章包含标题、摘要、封面、分类、标签、点赞数、收藏数、评论数等信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PageVO 分页视图对象
     * @see ArticleVO 文章视图对象
     */
    @Operation(summary = "获取所有的文章列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_ARTICLE_LIST_MAX_COUNT)
    @Parameters({
            @Parameter(name = "pageNum", description = "页码", required = true),
            @Parameter(name = "pageSize", description = "每页数量", required = true)
    })
    @GetMapping("/list")
    public ResponseResult<PageVO<List<ArticleVO>>> list(
            @NotNull Integer pageNum,
            @NotNull Integer pageSize
    ) {
        return ControllerUtils.messageHandler((() -> articleService.listAllArticle(pageNum, pageSize)));
    }

    /**
     * 获取推荐文章信息
     * <p>
     * 获取设置为推荐状态的文章列表，通常用于首页或特殊位置的文章推荐。
     * 推荐文章是由管理员手动设置的优质文章。
     *
     * @return 响应结果，包含推荐文章列表
     *         <ul>
     *             <li>成功时返回推荐文章列表，每个文章包含基本信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see RecommendArticleVO 推荐文章视图对象
     */
    @Operation(summary = "获取推荐的文章信息")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_RANDOM_ARTICLE_MAX_COUNT)
    @GetMapping("/recommend")
    public ResponseResult<List<RecommendArticleVO>> recommend() {
        return ControllerUtils.messageHandler((() -> articleService.listRecommendArticle()));
    }

    /**
     * 获取随机文章信息
     * <p>
     * 随机获取指定数量的文章，用于增加用户的文章发现机会。
     * 该接口通常用于侧边栏或页面底部的随机文章推荐功能。
     *
     * @return 响应结果，包含随机文章列表
     *         <ul>
     *             <li>成功时返回随机文章列表，每个文章包含基本信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see RandomArticleVO 随机文章视图对象
     */
    @Operation(summary = "获取随机的文章信息")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_RANDOM_ARTICLE_MAX_COUNT)
    @GetMapping("/random")
    public ResponseResult<List<RandomArticleVO>> random() {
        return ControllerUtils.messageHandler((() -> articleService.listRandomArticle()));
    }

    /**
     * 获取文章详情
     * <p>
     * 根据文章ID获取文章的完整详细信息，包括文章内容、分类、标签、统计数据等。
     * 该接口用于文章详情页面的数据展示。
     *
     * @param id 文章ID，必填参数，用于指定要获取的文章
     * @return 响应结果，包含文章详细信息
     *         <ul>
     *             <li>成功时返回文章详情，包含标题、内容、分类、标签、作者、创建时间、更新时间、点赞数、收藏数、评论数等完整信息</li>
     *             <li>失败时返回错误信息，可能是因为文章不存在或文章未公开</li>
     *         </ul>
     * @throws IllegalArgumentException 当文章ID为空时抛出
     * @see ArticleDetailVO 文章详情视图对象
     */
    @Operation(summary = "获取文章详情")
    @Parameter(name = "id", description = "文章id", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_ARTICLE_DETAIL_MAX_COUNT)
    @GetMapping("/detail/{id}")
    public ResponseResult<ArticleDetailVO> detail(@PathVariable("id") @NotNull Integer id) {
        return ControllerUtils.messageHandler((() -> articleService.getArticleDetail(id)));
    }

    /**
     * 获取相关文章信息
     * <p>
     * 根据指定文章的分类ID获取同分类下的其他文章，用于文章详情页面的相关文章推荐。
     * 会排除当前正在查看的文章，只返回同分类的其他文章。
     *
     * @param categoryId 分类ID，必填参数，用于指定文章分类
     * @param articleId 当前文章ID，必填参数，用于排除当前文章
     * @return 响应结果，包含相关文章列表
     *         <ul>
     *             <li>成功时返回相关文章列表，每个文章包含基本信息</li>
     *             <li>失败时返回错误信息，可能是因为分类不存在或文章不存在</li>
     *         </ul>
     * @throws IllegalArgumentException 当分类ID或文章ID为空时抛出
     * @see RelatedArticleVO 相关文章视图对象
     */
    @Operation(summary = "相关文章信息")
    @Parameters({
            @Parameter(name = "categoryId", description = "分类id", required = true),
            @Parameter(name = "articleId", description = "文章id", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_RELATED_ARTICLE_MAX_COUNT)
    @GetMapping("/related/{categoryId}/{articleId}")
    public ResponseResult<List<RelatedArticleVO>> related(
            @PathVariable("categoryId") @NotNull Integer categoryId,
            @PathVariable("articleId") @NotNull Integer articleId
    ) {
        return ControllerUtils.messageHandler((() -> articleService.relatedArticleList(categoryId, articleId)));
    }

    /**
     * 获取时间轴数据
     * <p>
     * 获取按时间线组织的文章数据，通常按年份和月份进行分组显示。
     * 该接口用于时间轴页面，展示博客的历史文章发布情况。
     *
     * @return 响应结果，包含时间轴数据
     *         <ul>
     *             <li>成功时返回时间轴数据，按时间分组的文章列表</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TimeLineVO 时间轴视图对象
     */
    @Operation(summary = "获取时间轴数据")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_TIME_LINE_MAX_COUNT)
    @GetMapping("/timeLine")
    public ResponseResult<List<TimeLineVO>> timeLine() {
        return ControllerUtils.messageHandler((articleService::listTimeLine));
    }


    /**
     * 获取分类或标签下的文章
     * <p>
     * 根据类型（分类或标签）和类型ID获取对应的文章列表。
     * 该接口用于分类页面和标签页面显示特定分类或标签下的所有文章。
     *
     * @param typeId 类型ID，必填参数，分类ID或标签ID
     * @param type 类型标识，必填参数，用于区分是分类还是标签
     * @return 响应结果，包含指定分类或标签下的文章列表
     *         <ul>
     *             <li>成功时返回文章列表，每个文章包含基本信息</li>
     *             <li>失败时返回错误信息，可能是因为类型ID不存在或类型参数无效</li>
     *         </ul>
     * @throws IllegalArgumentException 当类型ID或类型参数为空时抛出
     * @see CategoryArticleVO 分类文章视图对象
     */
    @Operation(summary = "获取分类与标签下的文章")
    @Parameters({
            @Parameter(name = "typeId", description = "类型id", required = true),
            @Parameter(name = "type", description = "类型", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_CATEGORY_MAX_COUNT)
    @GetMapping("/where/list/{typeId}")
    public ResponseResult<List<CategoryArticleVO>> listCategoryArticle(
            @NotNull @PathVariable("typeId") Long typeId,
            @NotNull @RequestParam("type") Integer type
    ) {
        return ControllerUtils.messageHandler(() -> articleService.listCategoryArticle(type, typeId));
    }


    /**
     * 增加文章访问量
     * <p>
     * 为指定文章的访问量计数器加1，用于统计文章的浏览次数。
     * 该接口通常在用户访问文章详情页面时调用。
     *
     * @param id 文章ID，必填参数，用于指定要增加访问量的文章
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为文章不存在</li>
     *         </ul>
     * @throws IllegalArgumentException 当文章ID为空时抛出
     */
    @Operation(summary = "文章访问量+1")
    @Parameter(name = "id", description = "文章id", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.VISIT_COUNT_MAX_COUNT)
    @GetMapping("/visit/{id}")
    public ResponseResult<Void> visit(@PathVariable("id") @NotNull Long id) {
        articleService.addVisitCount(id);
        return ControllerUtils.messageHandler(() -> null);
    }


    /**
     * 上传文章封面
     * <p>
     * 上传文章封面图片文件，支持JPG、JPEG、PNG、WEBP格式，文件大小限制为0.3MB。
     * 上传成功后返回图片的访问URL，用于文章发布时设置封面。
     * 该接口需要文章发布权限。
     *
     * @param articleCover 文章封面图片文件，必填参数，支持的格式：JPG、JPEG、PNG、WEBP，大小限制：0.3MB
     * @return 响应结果，包含上传后的图片访问URL
     *         <ul>
     *             <li>成功时返回图片的完整访问URL</li>
     *             <li>失败时返回错误信息，可能是因为文件格式不支持、文件过大或上传失败</li>
     *         </ul>
     * @see LogConst#UPLOAD_IMAGE 操作类型：上传图片
     */
    @PreAuthorize("hasAnyAuthority('blog:publish:article')")
    @Operation(summary = "上传文章封面")
    @Parameter(name = "articleCover", description = "文章封面")
    @LogAnnotation(module = "文章管理", operation = LogConst.UPLOAD_IMAGE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.UPLOAD_ARTICLE_COVER_MAX_COUNT)
    @PostMapping("/upload/articleCover")
    public ResponseResult<String> uploadArticleCover(@RequestParam("articleCover") MultipartFile articleCover) {
        return articleService.uploadArticleCover(articleCover);
    }


    /**
     * 发布文章
     * <p>
     * 创建并发布新文章，包括文章内容、分类、标签、封面等信息。
     * 文章发布后会自动设置创建时间和更新时间，并初始化统计数据。
     * 该接口需要文章发布权限，操作完成后会记录操作日志。
     *
     * @param articleDTO 文章数据传输对象，包含：
     *                  <ul>
     *                      <li>title: 文章标题，不能为空</li>
     *                      <li>content: 文章内容，不能为空</li>
     *                      <li>summary: 文章摘要</li>
     *                      <li>cover: 文章封面URL</li>
     *                      <li>categoryId: 分类ID，不能为空</li>
     *                      <li>tagIds: 标签ID列表</li>
     *                      <li>status: 文章状态（公开/私密）</li>
     *                      <li>isTop: 是否置顶</li>
     *                  </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为必填字段为空或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当必填字段为空时抛出
     * @see ArticleDTO 文章数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @PreAuthorize("hasAnyAuthority('blog:publish:article')")
    @Operation(summary = "发布文章")
    @Parameter(name = "articleDTO", description = "文章信息")
    @LogAnnotation(module = "文章管理", operation = LogConst.INSERT)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.PUBLISH_ARTICLE_MAX_COUNT)
    @PostMapping("/publish")
    public ResponseResult<Void> publish(@RequestBody @Valid ArticleDTO articleDTO) {
        return articleService.publish(articleDTO);
    }


    /**
     * 删除文章封面
     * <p>
     * 删除已上传的文章封面图片文件，通常在用户取消使用某个封面时调用。
     * 该接口会删除服务器上的图片文件，释放存储空间。
     * 需要文章发布权限。
     *
     * @param articleCoverUrl 文章封面URL，必填参数，要删除的封面图片的完整URL
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为文件不存在或删除失败</li>
     *         </ul>
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:publish:article')")
    @Operation(summary = "删除文章封面")
    @Parameter(name = "articleCover", description = "文章封面")
    @LogAnnotation(module = "发布错误", operation = LogConst.DELETE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DELETE_ARTICLE_COVER_MAX_COUNT)
    @GetMapping("/delete/articleCover")
    public ResponseResult<Void> deleteArticleCover(@RequestParam("articleCoverUrl") String articleCoverUrl) {
        return articleService.deleteArticleCover(articleCoverUrl);
    }


    /**
     * 上传文章图片
     * <p>
     * 上传文章内容中使用的图片文件，支持JPG、JPEG、PNG、WEBP格式，文件大小限制为0.3MB。
     * 上传成功后返回图片的访问URL，用于在文章编辑器中插入图片。
     * 该接口需要文章发布权限。
     *
     * @param articleImage 文章图片文件，必填参数，支持的格式：JPG、JPEG、PNG、WEBP，大小限制：0.3MB
     * @return 响应结果，包含上传后的图片访问URL
     *         <ul>
     *             <li>成功时返回图片的完整访问URL</li>
     *             <li>失败时返回错误信息，可能是因为文件格式不支持、文件过大或上传失败</li>
     *         </ul>
     * @see LogConst#UPLOAD_IMAGE 操作类型：上传图片
     */
    @PreAuthorize("hasAnyAuthority('blog:publish:article')")
    @Operation(summary = "上传文章图片")
    @Parameters({
            @Parameter(name = "articleImage", description = "文章图片"),
            @Parameter(name = "articleId", description = "文章id", required = true)
    })
    @LogAnnotation(module = "文章管理", operation = LogConst.UPLOAD_IMAGE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.UPLOAD_ARTICLE_IMAGE_MAX_COUNT)
    @PostMapping("/upload/articleImage")
    public ResponseResult<String> uploadArticleImage(
            @RequestParam("articleImage") MultipartFile articleImage
    ) {
        return articleService.uploadArticleImage(articleImage);
    }


    /**
     * 获取后台文章列表
     * <p>
     * 获取所有文章的列表信息，用于后台管理系统的文章管理页面。
     * 返回的数据包含文章的基本信息和状态，供管理员进行文章管理操作。
     * 该接口需要文章列表查看权限。
     *
     * @return 响应结果，包含文章列表
     *         <ul>
     *             <li>成功时返回文章列表，每个文章包含ID、标题、状态、分类、标签、创建时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see ArticleListVO 文章列表视图对象
     * @see LogConst#GET 操作类型：获取
     */
    @PreAuthorize("hasAnyAuthority('blog:article:list')")
    @Operation(summary = "获取所有的文章列表")
    @LogAnnotation(module = "文章管理", operation = LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_ALL_ARTICLE_LIST_MAX_COUNT)
    @GetMapping("/back/list")
    public ResponseResult<List<ArticleListVO>> listArticle() {
        return ControllerUtils.messageHandler(() -> articleService.listArticle());
    }


    /**
     * 搜索后台文章列表
     * <p>
     * 根据搜索条件在后台文章管理中进行文章搜索，支持按标题、内容、分类、标签、状态等条件进行组合搜索。
     * 该接口用于后台管理系统的文章搜索功能，需要文章搜索权限。
     *
     * @param searchArticleDTO 搜索条件数据传输对象，包含：
     *                        <ul>
     *                            <li>title: 文章标题关键词</li>
     *                            <li>categoryId: 分类ID</li>
     *                            <li>tagId: 标签ID</li>
     *                            <li>status: 文章状态</li>
     *                            <li>startTime: 开始时间</li>
     *                            <li>endTime: 结束时间</li>
     *                        </ul>
     * @return 响应结果，包含符合搜索条件的文章列表
     *         <ul>
     *             <li>成功时返回文章列表，每个文章包含管理所需的完整信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchArticleDTO 搜索文章数据传输对象
     * @see ArticleListVO 文章列表视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('blog:article:search')")
    @Operation(summary = "搜索文章列表")
    @Parameters({
            @Parameter(name = "searchArticleDTO", description = "搜索文章信息", required = true)
    })
    @LogAnnotation(module = "文章管理", operation = LogConst.SEARCH)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.SEARCH_ALL_ARTICLE_MAX_COUNT)
    @PostMapping("/back/search")
    public ResponseResult<List<ArticleListVO>> searchArticle(@RequestBody SearchArticleDTO searchArticleDTO) {
        return ControllerUtils.messageHandler(() -> articleService.searchArticle(searchArticleDTO));
    }


    /**
     * 修改文章状态
     * <p>
     * 更新文章的发布状态，支持公开、私密等状态切换。
     * 该操作用于控制文章的可见性，需要文章更新权限。
     * 操作完成后会记录操作日志。
     *
     * @param id 文章ID，必填参数，用于指定要修改状态的文章
     * @param status 文章状态，必填参数，新的文章状态值
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为文章不存在或状态值无效</li>
     *         </ul>
     * @throws IllegalArgumentException 当文章ID或状态为空时抛出
     * @see LogConst#UPDATE 操作类型：修改
     */
    @PreAuthorize("hasAnyAuthority('blog:article:update')")
    @Operation(summary = "修改文章状态")
    @Parameters({
            @Parameter(name = "id", description = "文章id", required = true),
            @Parameter(name = "status", description = "状态", required = true)
    })
    @LogAnnotation(module = "文章管理", operation = LogConst.UPDATE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.UPDATE_ARTICLE_STATUS_MAX_COUNT)
    @PostMapping("/back/update/status")
    public ResponseResult<Void> updateArticleStatus(
            @RequestParam("id") @NotNull Long id,
            @RequestParam("status") @NotNull Integer status
    ) {
        return articleService.updateStatus(id, status);
    }


    /**
     * 修改文章置顶状态
     * <p>
     * 设置或取消文章的置顶状态，置顶文章会在列表中优先显示。
     * 该操作用于突出重要文章，需要文章更新权限。
     * 操作完成后会记录操作日志。
     *
     * @param id 文章ID，必填参数，用于指定要修改置顶状态的文章
     * @param isTop 是否置顶，必填参数，1表示置顶，0表示取消置顶
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为文章不存在或置顶状态值无效</li>
     *         </ul>
     * @throws IllegalArgumentException 当文章ID或置顶状态为空时抛出
     * @see LogConst#UPDATE 操作类型：修改
     */
    @PreAuthorize("hasAnyAuthority('blog:article:update')")
    @Operation(summary = "修改文章是否顶置")
    @Parameters({
            @Parameter(name = "id", description = "文章id", required = true),
            @Parameter(name = "isTop", description = "是否顶置", required = true)
    })
    @LogAnnotation(module = "文章管理", operation = LogConst.UPDATE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.UPDATE_IS_TOP_MAX_COUNT)
    @PostMapping("/back/update/isTop")
    public ResponseResult<Void> updateArticleIsTop(
            @RequestParam("id") @NotNull Long id,
            @RequestParam("isTop") @NotNull Integer isTop
    ) {
        return articleService.updateIsTop(id, isTop);
    }


    /**
     * 回显文章数据
     * <p>
     * 获取文章的完整编辑数据，用于文章编辑页面的数据回显。
     * 返回的数据包含文章的所有可编辑字段，供编辑器进行数据填充。
     * 该接口需要文章回显权限。
     *
     * @param id 文章ID，必填参数，用于指定要回显的文章
     * @return 响应结果，包含文章的完整编辑数据
     *         <ul>
     *             <li>成功时返回文章数据传输对象，包含标题、内容、分类、标签、封面、状态等所有编辑字段</li>
     *             <li>失败时返回错误信息，可能是因为文章不存在或无权限访问</li>
     *         </ul>
     * @throws IllegalArgumentException 当文章ID为空时抛出
     * @see ArticleDTO 文章数据传输对象
     * @see LogConst#GET 操作类型：获取
     */
    @PreAuthorize("hasAnyAuthority('blog:article:echo')")
    @Operation(summary = "回显文章数据")
    @Parameters({
            @Parameter(name = "id", description = "文章id", required = true)
    })
    @LogAnnotation(module = "文章管理", operation = LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_ARTICLE_ECHO_MAX_COUNT)
    @GetMapping("/back/echo/{id}")
    public ResponseResult<ArticleDTO> getArticleEcho(@PathVariable("id") Long id) {
        return ControllerUtils.messageHandler(() -> articleService.getArticleDTO(id));
    }


    /**
     * 批量删除文章
     * <p>
     * 根据文章ID列表批量删除文章，同时会删除相关的标签关系、评论、点赞、收藏等关联数据。
     * 该操作不可逆，请谨慎使用。需要文章删除权限，操作完成后会记录操作日志。
     *
     * @param ids 文章ID列表，必填参数，要删除的文章ID集合
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示文章已成功删除</li>
     *             <li>失败时返回错误信息，可能是因为文章不存在、无权限删除或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当文章ID列表为空时抛出
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:article:delete')")
    @Operation(summary = "删除文章")
    @Parameters({
            @Parameter(name = "id", description = "文章id", required = true)
    })
    @LogAnnotation(module = "文章管理", operation = LogConst.DELETE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DELETE_ARTICLE_MAX_COUNT)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> deleteArticle(@RequestBody List<Long> ids) {
        return articleService.deleteArticle(ids);
    }
}
