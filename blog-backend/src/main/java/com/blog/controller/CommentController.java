package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.CheckBlacklist;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.CommentIsCheckDTO;
import com.blog.domain.dto.SearchCommentDTO;
import com.blog.domain.dto.UserCommentDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.ArticleCommentVO;
import com.blog.domain.vo.CommentListVO;
import com.blog.domain.vo.PageVO;
import com.blog.service.CommentService;
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
 * @description 评论相关接口
 * @since 2025/7/31 16:24
 */

@RestController
@Tag(name = "评论相关接口")
@RequestMapping("/comment")
@Validated
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 获取评论列表
     * <p>
     * 分页获取指定类型和ID的评论列表，支持文章评论和留言板评论。
     * 返回的评论按时间倒序排列，包含父评论和子评论的嵌套结构。
     * 该接口对所有用户开放，无需登录即可访问，但有访问频率限制。
     * 只返回已审核通过的评论内容。
     *
     * @param type 评论类型，必填参数
     *            <ul>
     *                <li>1: 文章评论</li>
     *                <li>2: 留言板评论</li>
     *            </ul>
     * @param typeId 评论关联的对象ID，必填参数
     *              <ul>
     *                  <li>当type=1时，为文章ID</li>
     *                  <li>当type=2时，为留言ID</li>
     *              </ul>
     * @param pageNum 页码，必填参数，从1开始
     * @param pageSize 每页数量，必填参数，建议10-50之间
     * @return 响应结果，包含分页的评论列表
     *         <ul>
     *             <li>成功时返回评论列表，包含评论内容、用户信息、点赞数、回复列表等</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see ArticleCommentVO 评论信息视图对象
     */
    @Operation(summary = "获取评论")
    @Parameters({
            @Parameter(name = "type", description = "评论类型", required = true),
            @Parameter(name = "typeId", description = "评论id", required = true),
            @Parameter(name = "pageNum", description = "页码", required = true),
            @Parameter(name = "pageSize", description = "每页数量", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_COMMENT_MAX_COUNT)
    @GetMapping("/getComment")
    public ResponseResult<PageVO<List<ArticleCommentVO>>> comment(
            @Valid @NotNull Integer type,
            @Valid @NotNull Integer typeId,
            @Valid @NotNull Integer pageNum,
            @Valid @NotNull Integer pageSize
    ) {
        return ControllerUtils.messageHandler((() -> commentService.getComment(type, typeId, pageNum, pageSize)));
    }

    /**
     * 用户添加评论
     * <p>
     * 用户发布新的评论内容，支持对文章或留言进行评论，也支持回复其他用户的评论。
     * 系统会自动检查用户是否在黑名单中，并对评论频率进行限制以防止恶意刷屏。
     * 发布的评论需要经过管理员审核才能在前台显示。
     * 该接口需要用户登录后才能访问。
     *
     * @param commentDTO 评论数据传输对象，包含评论内容和相关信息，不能为空
     *                  <ul>
     *                      <li>type: 评论类型（1-文章评论，2-留言评论）</li>
     *                      <li>typeId: 关联对象ID（文章ID或留言ID）</li>
     *                      <li>content: 评论内容，支持文本和表情符号</li>
     *                      <li>parentId: 父评论ID，回复评论时必填</li>
     *                      <li>replyUserId: 被回复用户ID，回复评论时必填</li>
     *                  </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态和提示信息</li>
     *             <li>失败时返回错误信息，可能是因为内容为空、用户在黑名单中或评论过于频繁</li>
     *         </ul>
     * @see CheckBlacklist 黑名单检查注解
     * @see UserCommentDTO 用户评论数据传输对象
     */
    @CheckBlacklist
    @Operation(summary = "用户添加评论")
    @Parameter(name = "commentDTO", description = "评论信息", required = true)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.USER_ADD_COMMENT_MAX_COUNT)
    @PostMapping("/auth/add/comment")
    public ResponseResult<String> userComment(@Valid @RequestBody UserCommentDTO commentDTO) {
        return commentService.userComment(commentDTO);
    }

    /**
     * 后台评论列表
     * <p>
     * 获取后台管理系统中的所有评论列表，包括已审核和未审核的内容。
     * 该接口仅限具有评论列表查看权限的管理员访问，用于评论内容的管理和审核。
     * 返回的数据包含评论的完整管理信息，操作会被记录到系统日志中。
     *
     * @return 响应结果，包含后台评论列表
     *         <ul>
     *             <li>成功时返回评论列表，每个评论包含ID、内容、用户信息、审核状态、发布时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see CommentListVO 后台评论列表视图对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:comment:list')")
    @Operation(summary = "后台评论列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="评论管理",operation= LogConst.GET)
    @GetMapping("/back/list")
    public ResponseResult<List<CommentListVO>> backList() {
        return ControllerUtils.messageHandler(() -> commentService.getBackCommentList(null));
    }

    /**
     * 搜索后台评论列表
     * <p>
     * 根据指定条件搜索后台评论列表，支持按评论内容、用户信息、审核状态、评论类型等条件进行筛选。
     * 该接口仅限具有评论搜索权限的管理员访问，用于快速定位特定的评论内容。
     * 操作会被记录到系统日志中。
     *
     * @param searchDTO 搜索条件数据传输对象，包含：
     *                 <ul>
     *                     <li>content: 评论内容关键词，可选参数，用于模糊匹配评论内容</li>
     *                     <li>isCheck: 审核状态，可选参数，用于筛选已审核或未审核的评论</li>
     *                     <li>type: 评论类型，可选参数，用于筛选文章评论或留言评论</li>
     *                     <li>username: 用户名，可选参数，用于筛选特定用户的评论</li>
     *                     <li>startTime: 开始时间，可选参数，用于筛选指定时间范围的评论</li>
     *                     <li>endTime: 结束时间，可选参数，用于筛选指定时间范围的评论</li>
     *                 </ul>
     * @return 响应结果，包含符合搜索条件的评论列表
     *         <ul>
     *             <li>成功时返回评论列表，每个评论包含ID、内容、用户信息、审核状态、发布时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchCommentDTO 评论搜索条件数据传输对象
     * @see CommentListVO 后台评论列表视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('blog:comment:search')")
    @Operation(summary = "搜索后台评论列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="评论管理",operation= LogConst.SEARCH)
    @PostMapping("/back/search")
    public ResponseResult<List<CommentListVO>> backList(@RequestBody SearchCommentDTO searchDTO) {
        return ControllerUtils.messageHandler(() -> commentService.getBackCommentList(searchDTO));
    }

    /**
     * 修改评论审核状态
     * <p>
     * 管理员审核评论内容，设置评论是否通过审核。通过审核的评论将在前台评论区显示，
     * 未通过审核的评论将被隐藏。该操作仅限具有评论审核权限的管理员执行，
     * 操作会被记录到系统日志中。
     *
     * @param commentIsCheckDTO 评论审核状态数据传输对象，包含：
     *                         <ul>
     *                             <li>id: 评论ID，不能为空，用于指定要审核的评论</li>
     *                             <li>isCheck: 审核状态，不能为空，true表示通过审核，false表示不通过</li>
     *                         </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示审核状态已成功更新</li>
     *             <li>失败时返回错误信息，可能是因为评论不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当必填字段为空时抛出
     * @see CommentIsCheckDTO 评论审核状态数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @PreAuthorize("hasAnyAuthority('blog:comment:isCheck')")
    @Operation(summary = "修改评论是否通过")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="评论管理",operation= LogConst.UPDATE)
    @PostMapping("/back/isCheck")
    public ResponseResult<Void> isCheck(@RequestBody @Valid CommentIsCheckDTO commentIsCheckDTO) {
        return commentService.isCheckComment(commentIsCheckDTO);
    }

    /**
     * 删除评论
     * <p>
     * 删除指定的评论内容，该操作将永久删除评论数据，不可恢复。
     * 删除评论时会同时删除该评论下的所有回复内容和相关的点赞、收藏记录。
     * 该操作仅限具有评论删除权限的管理员执行，操作会被记录到系统日志中。
     * 建议在删除前确认评论内容确实需要删除。
     *
     * @param id 评论ID，不能为空，用于指定要删除的评论
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示指定的评论已成功删除</li>
     *             <li>失败时返回错误信息，可能是因为评论不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当评论ID为空时抛出
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:comment:delete')")
    @Operation(summary = "删除评论")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="评论管理",operation= LogConst.DELETE)
    @DeleteMapping("/back/delete/{id}")
    public ResponseResult<Void> delete(@PathVariable("id") Long id) {
        return commentService.deleteComment(id);
    }

}
