package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.SearchTagDTO;
import com.blog.domain.dto.TagDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.TagVO;
import com.blog.service.TagService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author haibara
 * @description 标签相关接口
 * @since 2025/7/30 19:41
 */

@RestController
@Tag(name = "标签相关接口")
@RequestMapping("/tag")
@Validated
public class TagController {


    @Resource
    private TagService tagService;


    /**
     * 获取所有标签列表（前台）
     * <p>
     * 获取系统中所有可见的标签信息，用于前台文章标签展示。
     * 该接口使用访问限制注解来防止频繁调用。
     *
     * @return 响应结果，包含所有标签列表
     *         <ul>
     *             <li>成功时返回标签列表，每个标签包含基本信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TagVO 标签信息视图对象
     */
    @Operation(summary = "获取标签列表")
    @GetMapping("/list")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_ALL_TAG_MAX_COUNT)
    public ResponseResult<List<TagVO>> list() {
        return ControllerUtils.messageHandler(() -> tagService.listAllTag());
    }


    /**
     * 新增标签（文章列表使用）
     * <p>
     * 创建新的文章标签，用于文章列表页面的标签添加功能。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param tagDTO 标签数据传输对象，包含标签的基本信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TagDTO 标签数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @Operation(summary = "新增标签-文章列表")
    @PreAuthorize("hasAnyAuthority('blog:tag:add')")
    @LogAnnotation(module="标签管理",operation= LogConst.INSERT)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PutMapping()
    public ResponseResult<Void> addTag(@RequestBody @Valid TagDTO tagDTO) {
        return tagService.addTag(tagDTO);
    }


    /**
     * 获取标签列表（后台管理）
     * <p>
     * 获取系统中所有标签信息，用于后台管理页面的标签管理。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @return 响应结果，包含所有标签列表
     *         <ul>
     *             <li>成功时返回标签列表，包含完整的标签信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TagVO 标签信息视图对象
     * @see LogConst#GET 操作类型：获取
     */
    @Operation(summary = "获取标签列表")
    @PreAuthorize("hasAnyAuthority('blog:tag:list')")
    @LogAnnotation(module="标签管理",operation= LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/back/list")
    public ResponseResult<List<TagVO>> listArticleTag() {
        return ControllerUtils.messageHandler(() -> tagService.listAllTag());
    }


    /**
     * 搜索标签
     * <p>
     * 根据搜索条件查询符合条件的标签列表，用于后台管理的标签搜索功能。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param searchTagDTO 标签搜索条件数据传输对象
     * @return 响应结果，包含符合搜索条件的标签列表
     *         <ul>
     *             <li>成功时返回标签列表</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchTagDTO 标签搜索条件数据传输对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @Operation(summary = "搜索标签列表")
    @PreAuthorize("hasAnyAuthority('blog:tag:search')")
    @LogAnnotation(module="标签管理",operation= LogConst.SEARCH)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/back/search")
    public ResponseResult<List<TagVO>> searchTag(@RequestBody SearchTagDTO searchTagDTO) {
        return ControllerUtils.messageHandler(() -> tagService.searchTag(searchTagDTO));
    }


    /**
     * 根据ID查询标签
     * <p>
     * 获取指定ID的标签详细信息，用于后台管理的标签编辑功能。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param id 标签ID，必填参数
     * @return 响应结果，包含标签详细信息
     *         <ul>
     *             <li>成功时返回标签信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TagVO 标签信息视图对象
     * @see LogConst#GET 操作类型：获取
     */
    @Operation(summary = "根据id查询标签")
    @PreAuthorize("hasAnyAuthority('blog:tag:search')")
    @LogAnnotation(module="标签管理",operation= LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/back/get/{id}")
    public ResponseResult<TagVO> getTagById(@PathVariable(value = "id") Long id) {
        return ControllerUtils.messageHandler(() -> tagService.getTagById(id));
    }


    /**
     * 新增标签（标签列表使用）
     * <p>
     * 在标签管理页面创建新的标签。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param tagDTO 标签数据传输对象，包含标签的基本信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TagDTO 标签数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @Operation(summary = "新增标签-标签列表")
    @PreAuthorize("hasAnyAuthority('blog:tag:add')")
    @LogAnnotation(module="标签管理",operation= LogConst.INSERT)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PutMapping("/back/add")
    public ResponseResult<Void> addOrUpdateTag(@RequestBody @Valid TagDTO tagDTO) {
        return tagService.addOrUpdateTag(tagDTO.setId(null));
    }


    /**
     * 修改标签信息
     * <p>
     * 更新现有标签的信息。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param tagDTO 标签数据传输对象，包含更新后的标签信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TagDTO 标签数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @Operation(summary = "修改标签")
    @PreAuthorize("hasAnyAuthority('blog:tag:update')")
    @LogAnnotation(module="标签管理",operation= LogConst.UPDATE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/back/update")
    public ResponseResult<Void> updateTag(@RequestBody @Valid TagDTO tagDTO) {
        return tagService.addOrUpdateTag(tagDTO);
    }


    /**
     * 删除标签
     * <p>
     * 批量删除指定ID的标签。删除标签时会同时检查是否有关联的文章。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param ids 要删除的标签ID列表
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为标签仍在使用中</li>
     *         </ul>
     * @see LogConst#DELETE 操作类型：删除
     */
    @Operation(summary = "删除标签")
    @PreAuthorize("hasAnyAuthority('blog:tag:delete')")
    @LogAnnotation(module="标签管理",operation= LogConst.DELETE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> deleteTag(@RequestBody List<Long> ids) {
        return tagService.deleteTagByIds(ids);
    }
}
