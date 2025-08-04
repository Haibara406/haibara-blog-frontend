package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.CheckBlacklist;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.FavoriteIsCheckDTO;
import com.blog.domain.dto.SearchFavoriteDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.FavoriteListVO;
import com.blog.service.FavoriteService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author haibara
 * @description 收藏相关接口
 * @since 2025/7/31 21:09
 */

@RestController
@Tag(name = "收藏相关接口")
@RequestMapping("/favorite")
@Validated
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    /**
     * 用户收藏操作
     * <p>
     * 用户对指定内容进行收藏操作，支持对文章、留言等不同类型的内容进行收藏。
     * 系统会自动检查用户是否在黑名单中，并对收藏频率进行限制以防止恶意刷收藏。
     * 如果用户已经收藏过该内容，则不会重复收藏。
     * 该接口需要用户登录后才能访问。
     *
     * @param type 收藏类型，必填参数
     *            <ul>
     *                <li>1: 文章收藏</li>
     *                <li>2: 留言收藏</li>
     *            </ul>
     * @param typeId 收藏对象的ID，可选参数
     *              <ul>
     *                  <li>当type=1时，为文章ID</li>
     *                  <li>当type=2时，为留言ID</li>
     *              </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示收藏操作已完成</li>
     *             <li>失败时返回错误信息，可能是因为用户在黑名单中、收藏过于频繁或已经收藏过</li>
     *         </ul>
     * @see CheckBlacklist 黑名单检查注解
     */
    @CheckBlacklist
    @Operation(summary = "收藏")
    @Parameters({
            @Parameter(name = "type", description = "收藏类型", required = true),
            @Parameter(name = "typeId", description = "收藏id", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.USER_FAVORITE_MAX_COUNT)
    @PostMapping("/auth/favorite")
    public ResponseResult<Void> favorite(
            @Valid @NotNull @RequestParam("type") Integer type,
            @RequestParam(value = "typeId", required = false) Long typeId
    ) {
        return favoriteService.userFavorite(type, typeId);
    }

    /**
     * 用户取消收藏操作
     * <p>
     * 用户取消对指定内容的收藏操作，支持对文章、留言等不同类型的内容取消收藏。
     * 系统会自动检查用户是否在黑名单中，并对取消收藏频率进行限制以防止恶意操作。
     * 如果用户没有收藏过该内容，则操作无效果但不会报错。
     * 该接口需要用户登录后才能访问。
     *
     * @param type 收藏类型，必填参数
     *            <ul>
     *                <li>1: 文章收藏</li>
     *                <li>2: 留言收藏</li>
     *            </ul>
     * @param typeId 收藏对象的ID，可选参数
     *              <ul>
     *                  <li>当type=1时，为文章ID</li>
     *                  <li>当type=2时，为留言ID</li>
     *              </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示取消收藏操作已完成</li>
     *             <li>失败时返回错误信息，可能是因为用户在黑名单中或操作过于频繁</li>
     *         </ul>
     * @see CheckBlacklist 黑名单检查注解
     */
    @CheckBlacklist
    @Operation(summary = "取消收藏")
    @Parameters({
            @Parameter(name = "type", description = "收藏类型", required = true),
            @Parameter(name = "typeId", description = "收藏id", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.USER_CANCEL_FAVORITE_MAX_COUNT)
    @DeleteMapping("/auth/favorite")
    public ResponseResult<Void> cancelFavorite(
            @Valid @NotNull @RequestParam("type") Integer type,
            @RequestParam(value = "typeId", required = false) Integer typeId
    ) {
        return favoriteService.cancelFavorite(type, typeId);
    }

    /**
     * 检查用户是否已收藏
     * <p>
     * 检查当前登录用户是否对指定内容进行了收藏操作，支持对文章、留言等不同类型的内容进行检查。
     * 该接口对所有用户开放，无需特殊权限，但有访问频率限制以防止恶意查询。
     * 如果用户未登录，将返回未收藏状态。
     *
     * @param type 收藏类型，必填参数
     *            <ul>
     *                <li>1: 文章收藏</li>
     *                <li>2: 留言收藏</li>
     *            </ul>
     * @param typeId 收藏对象的ID，可选参数
     *              <ul>
     *                  <li>当type=1时，为文章ID</li>
     *                  <li>当type=2时，为留言ID</li>
     *              </ul>
     * @return 响应结果，包含收藏状态
     *         <ul>
     *             <li>成功时返回布尔值，true表示已收藏，false表示未收藏</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     */
    @Operation(summary = "是否已经收藏")
    @Parameters({
            @Parameter(name = "type", description = "收藏类型", required = true),
            @Parameter(name = "typeId", description = "收藏id", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.CHECK_IS_FAVORITE_MAX_COUNT)
    @GetMapping("/whether/favorite")
    public ResponseResult<Boolean> isFavorite(
            @Valid @NotNull @RequestParam("type") Integer type,
            @RequestParam(value = "typeId", required = false) Integer typeId
    ) {
        return ControllerUtils.messageHandler((() -> favoriteService.isFavorite(type, typeId)));
    }

    /**
     * 后台收藏列表
     * <p>
     * 获取后台管理系统中的所有收藏记录列表，包括已审核和未审核的内容。
     * 该接口仅限具有收藏列表查看权限的管理员访问，用于收藏内容的管理和审核。
     * 返回的数据包含收藏的完整管理信息，操作会被记录到系统日志中。
     *
     * @return 响应结果，包含后台收藏列表
     *         <ul>
     *             <li>成功时返回收藏列表，每个收藏包含ID、类型、内容、用户信息、审核状态、收藏时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see FavoriteListVO 后台收藏列表视图对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:favorite:list')")
    @Operation(summary = "后台收藏列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="收藏管理",operation= LogConst.GET)
    @GetMapping("/back/list")
    public ResponseResult<List<FavoriteListVO>> backList() {
        return ControllerUtils.messageHandler(() -> favoriteService.getBackFavoriteList(null));
    }

    /**
     * 搜索后台收藏列表
     * <p>
     * 根据指定条件搜索后台收藏列表，支持按收藏类型、用户信息、审核状态、收藏时间等条件进行筛选。
     * 该接口仅限具有收藏搜索权限的管理员访问，用于快速定位特定的收藏记录。
     * 操作会被记录到系统日志中。
     *
     * @param searchDTO 搜索条件数据传输对象，包含：
     *                 <ul>
     *                     <li>type: 收藏类型，可选参数，用于筛选文章收藏或留言收藏</li>
     *                     <li>isCheck: 审核状态，可选参数，用于筛选已审核或未审核的收藏</li>
     *                     <li>username: 用户名，可选参数，用于筛选特定用户的收藏</li>
     *                     <li>startTime: 开始时间，可选参数，用于筛选指定时间范围的收藏</li>
     *                     <li>endTime: 结束时间，可选参数，用于筛选指定时间范围的收藏</li>
     *                 </ul>
     * @return 响应结果，包含符合搜索条件的收藏列表
     *         <ul>
     *             <li>成功时返回收藏列表，每个收藏包含ID、类型、内容、用户信息、审核状态、收藏时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchFavoriteDTO 收藏搜索条件数据传输对象
     * @see FavoriteListVO 后台收藏列表视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('blog:favorite:search')")
    @Operation(summary = "搜索后台收藏列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="收藏管理",operation= LogConst.SEARCH)
    @PostMapping("/back/search")
    public ResponseResult<List<FavoriteListVO>> backList(@RequestBody SearchFavoriteDTO searchDTO) {
        return ControllerUtils.messageHandler(() -> favoriteService.getBackFavoriteList(searchDTO));
    }

    /**
     * 修改收藏审核状态
     * <p>
     * 管理员审核收藏记录，设置收藏是否通过审核。通过审核的收藏将在相关统计中显示，
     * 未通过审核的收藏将被隐藏。该操作仅限具有收藏审核权限的管理员执行，
     * 操作会被记录到系统日志中。
     *
     * @param favoriteIsCheckDTO 收藏审核状态数据传输对象，包含：
     *                          <ul>
     *                              <li>id: 收藏ID，不能为空，用于指定要审核的收藏</li>
     *                              <li>isCheck: 审核状态，不能为空，true表示通过审核，false表示不通过</li>
     *                          </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示审核状态已成功更新</li>
     *             <li>失败时返回错误信息，可能是因为收藏不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当必填字段为空时抛出
     * @see FavoriteIsCheckDTO 收藏审核状态数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @PreAuthorize("hasAnyAuthority('blog:favorite:isCheck')")
    @Operation(summary = "修改收藏是否通过")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="收藏管理",operation= LogConst.UPDATE)
    @PostMapping("/back/isCheck")
    public ResponseResult<Void> isCheck(@RequestBody @Valid FavoriteIsCheckDTO favoriteIsCheckDTO) {
        return favoriteService.isCheckFavorite(favoriteIsCheckDTO);
    }

    /**
     * 批量删除收藏
     * <p>
     * 删除指定的收藏记录，该操作将永久删除收藏数据，不可恢复。
     * 支持批量删除多个收藏记录，删除操作会同时清理相关的统计数据。
     * 该操作仅限具有收藏删除权限的管理员执行，操作会被记录到系统日志中。
     * 建议在删除前确认收藏记录确实需要删除。
     *
     * @param ids 收藏ID列表，不能为空，用于指定要删除的收藏记录
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示指定的收藏记录已成功删除</li>
     *             <li>失败时返回错误信息，可能是因为收藏不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当收藏ID列表为空时抛出
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:favorite:delete')")
    @Operation(summary = "删除收藏")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="收藏管理",operation= LogConst.DELETE)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> delete(@RequestBody List<Long> ids) {
        return favoriteService.deleteFavorite(ids);
    }

}
