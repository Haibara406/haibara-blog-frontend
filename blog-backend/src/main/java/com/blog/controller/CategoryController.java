package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.CategoryDTO;
import com.blog.domain.dto.SearchCategoryDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.CategoryVO;
import com.blog.service.CategoryService;
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
 * @description 分类相关接口
 * @since 2025/7/30 19:09
 */

@RestController
@Tag(name = "分类相关接口")
@RequestMapping("/category")
@Validated
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取所有分类列表（前台）
     * <p>
     * 获取系统中所有可见的分类信息，用于前台文章分类展示。
     * 该接口使用访问限制注解来防止频繁调用。
     *
     * @return 响应结果，包含所有分类列表
     *         <ul>
     *             <li>成功时返回分类列表，每个分类包含基本信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see CategoryVO 分类信息视图对象
     */
    @Operation(summary = "获取所有分类")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_ALL_CATEGORY_MAX_COUNT)
    @GetMapping("/list")
    public ResponseResult<List<CategoryVO>> listAllCategory() {
        return ControllerUtils.messageHandler((categoryService::listAllCategory));
    }

    /**
     * 新增分类（文章列表使用）
     * <p>
     * 创建新的文章分类，用于文章列表页面的分类添加功能。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param categoryDTO 分类数据传输对象，包含分类的基本信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see CategoryDTO 分类数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @Operation(summary = "新增分类-文章列表")
    @PreAuthorize("hasAnyAuthority('blog:category:add')")
    @LogAnnotation(module="新增分类",operation= LogConst.INSERT)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PutMapping()
    public ResponseResult<Void> addCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        return categoryService.addCategory(categoryDTO);
    }

    /**
     * 获取分类列表（后台管理）
     * <p>
     * 获取系统中所有分类信息，用于后台管理页面的分类管理。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @return 响应结果，包含所有分类列表
     *         <ul>
     *             <li>成功时返回分类列表，包含完整的分类信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see CategoryVO 分类信息视图对象
     * @see LogConst#GET 操作类型：获取
     */
    @Operation(summary = "获取分类列表")
    @PreAuthorize("hasAnyAuthority('blog:category:list')")
    @LogAnnotation(module="分类管理",operation= LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/back/list")
    public ResponseResult<List<CategoryVO>> listArticleCategory() {
        return ControllerUtils.messageHandler((categoryService::listAllCategory));
    }

    /**
     * 搜索分类
     * <p>
     * 根据搜索条件查询符合条件的分类列表，用于后台管理的分类搜索功能。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param searchCategoryDTO 分类搜索条件数据传输对象
     * @return 响应结果，包含符合搜索条件的分类列表
     *         <ul>
     *             <li>成功时返回分类列表</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchCategoryDTO 分类搜索条件数据传输对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @Operation(summary = "搜索分类列表")
    @PreAuthorize("hasAnyAuthority('blog:category:search')")
    @LogAnnotation(module="分类管理",operation= LogConst.SEARCH)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/back/search")
    public ResponseResult<List<CategoryVO>> searchCategory(@RequestBody SearchCategoryDTO searchCategoryDTO) {
        return ControllerUtils.messageHandler(() -> categoryService.searchCategory(searchCategoryDTO));
    }

    /**
     * 根据ID查询分类
     * <p>
     * 获取指定ID的分类详细信息，用于后台管理的分类编辑功能。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param id 分类ID，必填参数
     * @return 响应结果，包含分类详细信息
     *         <ul>
     *             <li>成功时返回分类信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see CategoryVO 分类信息视图对象
     * @see LogConst#GET 操作类型：获取
     */
    @Operation(summary = "根据id查询分类")
    @PreAuthorize("hasAnyAuthority('blog:category:search')")
    @LogAnnotation(module="分类管理",operation= LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/back/get/{id}")
    public ResponseResult<CategoryVO> getCategoryById(@PathVariable(value = "id") Long id) {
        return ControllerUtils.messageHandler(() -> categoryService.getCategoryById(id));
    }

    /**
     * 新增分类（分类列表使用）
     * <p>
     * 在分类管理页面创建新的分类。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param categoryDTO 分类数据传输对象，包含分类的基本信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see CategoryDTO 分类数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @Operation(summary = "新增分类-分类列表")
    @PreAuthorize("hasAnyAuthority('blog:category:add')")
    @LogAnnotation(module="分类管理",operation= LogConst.INSERT)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PutMapping("/back/add")
    public ResponseResult<Void> addOrUpdateCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        return categoryService.addOrUpdateCategory(categoryDTO.setId(null));
    }

    /**
     * 修改分类信息
     * <p>
     * 更新现有分类的信息。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param categoryDTO 分类数据传输对象，包含更新后的分类信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see CategoryDTO 分类数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @Operation(summary = "修改分类")
    @PreAuthorize("hasAnyAuthority('blog:category:update')")
    @LogAnnotation(module="分类管理",operation= LogConst.UPDATE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/back/update")
    public ResponseResult<Void> updateCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        return categoryService.addOrUpdateCategory(categoryDTO);
    }

    /**
     * 删除分类
     * <p>
     * 批量删除指定ID的分类。删除分类时会同时处理相关的文章分类关联。
     * 需要管理员权限，操作会被记录到系统日志中。
     *
     * @param ids 要删除的分类ID列表
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LogConst#DELETE 操作类型：删除
     */
    @Operation(summary = "删除分类")
    @PreAuthorize("hasAnyAuthority('blog:category:delete')")
    @LogAnnotation(module="分类管理",operation= LogConst.DELETE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> deleteCategory(@RequestBody List<Long> ids) {
        return categoryService.deleteCategoryByIds(ids);
    }
}
