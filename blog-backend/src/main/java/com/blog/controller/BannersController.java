package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.entity.Banners;
import com.blog.domain.response.ResponseResult;
import com.blog.service.BannersService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 首页轮播图管理控制器
 * <p>
 * 提供首页顶部轮播图的完整管理功能，包括轮播图的查询、上传、删除和排序等操作。
 * 支持前台展示和后台管理两套接口，确保轮播图的正常展示和便捷管理。
 *
 * @author haibara
 * @description 首页顶部轮播图相关接口
 * @since 2025/8/1 21:41
 */
@RestController
@RequestMapping("banners")
@Tag(name = "轮播图相关接口")
@Validated
public class BannersController {

    @Resource
    private BannersService bannersService;

    /**
     * 前台获取首页轮播图列表
     * <p>
     * 获取前台首页展示的轮播图URL列表，按照设定的顺序返回。
     * 该接口无需权限验证，用于前台首页轮播图展示。
     *
     * @return 响应结果，包含轮播图URL列表
     *         <ul>
     *             <li>成功时返回轮播图URL字符串列表</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LogConst#GET 操作类型：查询
     */
    @Operation(summary = "前台获取所有前台首页Banner图片")
    @LogAnnotation(module = "信息管理", operation = LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_BANNERS_MAX_COUNT)
    @GetMapping("/list")
    public ResponseResult<List<String>> getBanners() {
        return ControllerUtils.messageHandler(() -> bannersService.getBanners());
    }

    /**
     * 后台获取轮播图列表
     * <p>
     * 获取后台管理系统中的轮播图详细信息列表，包含轮播图的ID、URL、排序等完整信息。
     * 该接口需要管理员权限，用于后台轮播图管理页面。
     *
     * @return 响应结果，包含轮播图详细信息列表
     *         <ul>
     *             <li>成功时返回轮播图实体列表，包含ID、URL、排序、创建时间等信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see Banners 轮播图实体对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:banner:list')")
    @Operation(summary = "后台获取所有前台首页Banner图片")
    @LogAnnotation(module = "信息管理", operation = LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.BACK_GET_BANNERS_MAX_COUNT)
    @GetMapping("/back/list")
    public ResponseResult<List<Banners>> backGetBanners() {
        return ControllerUtils.messageHandler(() -> bannersService.backGetBanners());
    }

    /**
     * 删除轮播图
     * <p>
     * 根据轮播图ID删除指定的轮播图，同时会删除文件存储服务中的实际图片文件。
     * 删除操作不可逆，请谨慎操作。
     *
     * @param id 轮播图ID，必需参数
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回删除成功信息</li>
     *             <li>失败时返回错误信息（如轮播图不存在、文件删除失败等）</li>
     *         </ul>
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:banner:delete')")
    @Operation(summary = "删除前台首页Banner图片")
    @LogAnnotation(module = "信息管理", operation = LogConst.DELETE)
    @Parameter(name = "id", description = "Banner ID", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @DeleteMapping("/{id}")
    public ResponseResult<String> delete(@PathVariable("id") Long id) {
        return bannersService.removeBannerById(id);
    }

    /**
     * 上传轮播图
     * <p>
     * 上传新的轮播图到首页轮播图列表中，图片会被上传到文件存储服务。
     * 新上传的轮播图会自动添加到轮播图列表的末尾，可以通过排序功能调整顺序。
     *
     * @param bannerImage 轮播图文件，支持常见的图片格式（JPG、PNG、GIF等）
     * @return 响应结果，包含上传后的轮播图信息
     *         <ul>
     *             <li>成功时返回轮播图实体对象，包含ID、URL等信息</li>
     *             <li>失败时返回错误信息（如文件格式不支持、文件过大等）</li>
     *         </ul>
     * @see Banners 轮播图实体对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @PreAuthorize("hasAnyAuthority('blog:banner:add')")
    @Operation(summary = "添加前台首页Banner图片")
    @LogAnnotation(module = "信息管理", operation = LogConst.INSERT)
    @Parameter(name = "bannerImage", description = "Banner图片", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/upload/banner")
    public ResponseResult<Banners> uploadArticleImage(
            @RequestParam("bannerImage") MultipartFile bannerImage
    ) {
        return bannersService.uploadBannerImage(bannerImage);
    }

    /**
     * 更新轮播图排序
     * <p>
     * 批量更新轮播图的显示顺序，可以调整轮播图在首页的展示顺序。
     * 前台轮播图会按照设定的排序值从小到大依次展示。
     *
     * @param Banners 轮播图列表，包含要更新排序的轮播图ID和新的排序值
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回排序更新成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see Banners 轮播图实体对象
     * @see LogConst#UPDATE 操作类型：更新
     */
    @PreAuthorize("hasAnyAuthority('blog:banner:update')")
    @Operation(summary = "更新前台首页Banner图片顺序")
    @LogAnnotation(module = "信息管理", operation = LogConst.UPDATE)
    @Parameter(name = "SortOrders", description = "顺序", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PutMapping("/update/sort/order")
    public ResponseResult<String> updateSortOrder(@RequestBody List<Banners> Banners) {
        return bannersService.updateSortOrder(Banners);
    }
}
