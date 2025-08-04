package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.CheckBlacklist;
import com.blog.constants.AccessLimitConst;
import com.blog.domain.entity.Like;
import com.blog.domain.response.ResponseResult;
import com.blog.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author haibara
 * @description 点赞相关接口
 * @since 2025/7/31 22:23
 */

@RestController
@Tag(name = "点赞相关接口")
@RequestMapping("/like")
@Validated
public class LikeController {

    @Resource
    private LikeService likeService;

    /**
     * 用户点赞操作
     * <p>
     * 用户对指定内容进行点赞操作，支持对文章、评论、留言等不同类型的内容进行点赞。
     * 系统会自动检查用户是否在黑名单中，并对点赞频率进行限制以防止恶意刷赞。
     * 如果用户已经对该内容点过赞，则不会重复点赞。
     * 该接口需要用户登录后才能访问。
     *
     * @param type 点赞类型，必填参数
     *            <ul>
     *                <li>1: 文章点赞</li>
     *                <li>2: 评论点赞</li>
     *                <li>3: 留言点赞</li>
     *            </ul>
     * @param typeId 点赞对象的ID，必填参数
     *              <ul>
     *                  <li>当type=1时，为文章ID</li>
     *                  <li>当type=2时，为评论ID</li>
     *                  <li>当type=3时，为留言ID</li>
     *              </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示点赞操作已完成</li>
     *             <li>失败时返回错误信息，可能是因为用户在黑名单中、点赞过于频繁或已经点过赞</li>
     *         </ul>
     * @see CheckBlacklist 黑名单检查注解
     */
    @CheckBlacklist
    @Operation(summary = "点赞")
    @Parameters({
            @Parameter(name = "type", description = "点赞类型", required = true),
            @Parameter(name = "typeId", description = "点赞id", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.USER_LIKE_MAX_COUNT)
    @PostMapping("/auth/like")
    public ResponseResult<Void> like(
            @RequestParam("type") @Valid @NotNull Integer type,
            @RequestParam("typeId") @Valid @NotNull Integer typeId
    ) {
        return likeService.userLike(type, typeId);
    }

    /**
     * 用户取消点赞操作
     * <p>
     * 用户取消对指定内容的点赞操作，支持对文章、评论、留言等不同类型的内容取消点赞。
     * 系统会自动检查用户是否在黑名单中，并对取消点赞频率进行限制以防止恶意操作。
     * 如果用户没有对该内容点过赞，则操作无效果但不会报错。
     * 该接口需要用户登录后才能访问。
     *
     * @param type 点赞类型，必填参数
     *            <ul>
     *                <li>1: 文章点赞</li>
     *                <li>2: 评论点赞</li>
     *                <li>3: 留言点赞</li>
     *            </ul>
     * @param typeId 点赞对象的ID，必填参数
     *              <ul>
     *                  <li>当type=1时，为文章ID</li>
     *                  <li>当type=2时，为评论ID</li>
     *                  <li>当type=3时，为留言ID</li>
     *              </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示取消点赞操作已完成</li>
     *             <li>失败时返回错误信息，可能是因为用户在黑名单中或操作过于频繁</li>
     *         </ul>
     * @see CheckBlacklist 黑名单检查注解
     */
    @CheckBlacklist
    @Operation(summary = "取消点赞")
    @Parameters({
            @Parameter(name = "type", description = "点赞类型", required = true),
            @Parameter(name = "typeId", description = "点赞id", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.USER_CANCEL_LIKE_MAX_COUNT)
    @DeleteMapping("/auth/like")
    public ResponseResult<Void> cancelLike(
            @RequestParam("type") @Valid @NotNull Integer type,
            @RequestParam("typeId") @Valid @NotNull Integer typeId
    ) {
        return likeService.cancelLike(type, typeId);
    }

    /**
     * 检查用户是否已点赞
     * <p>
     * 检查当前登录用户是否对指定内容进行了点赞操作，支持对文章、评论、留言等不同类型的内容进行检查。
     * 该接口对所有用户开放，无需特殊权限，但有访问频率限制以防止恶意查询。
     * 如果用户未登录，将返回未点赞状态。
     *
     * @param type 点赞类型，必填参数
     *            <ul>
     *                <li>1: 文章点赞</li>
     *                <li>2: 评论点赞</li>
     *                <li>3: 留言点赞</li>
     *            </ul>
     * @param typeId 点赞对象的ID，可选参数
     *              <ul>
     *                  <li>当type=1时，为文章ID，必须提供</li>
     *                  <li>当type=2时，为评论ID，可选（不提供时查询用户所有评论点赞）</li>
     *                  <li>当type=3时，为留言ID，可选（不提供时查询用户所有留言点赞）</li>
     *              </ul>
     * @return 响应结果，包含点赞记录列表
     *         <ul>
     *             <li>成功时返回点赞记录列表，如果未点赞则返回空列表</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see Like 点赞实体对象
     */
    @Operation(summary = "是否已经点赞")
    @Parameters({
            @Parameter(name = "type", description = "点赞类型", required = true),
            @Parameter(name = "typeId", description = "点赞id", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.CHECK_IS_LIKE_MAX_COUNT)
    @GetMapping("/whether/like")
    public ResponseResult<List<Like>> isLike(
            @Valid @NotNull @RequestParam("type") Integer type,
            @RequestParam(value = "typeId", required = false) Integer typeId
    ) {
        return likeService.isLike(type, typeId);
    }
}
