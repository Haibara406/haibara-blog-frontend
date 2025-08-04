package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.CheckBlacklist;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.constants.ValidationConstants;
import com.blog.domain.dto.LinkDTO;
import com.blog.domain.dto.LinkIsCheckDTO;
import com.blog.domain.dto.SearchLinkDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LinkListVO;
import com.blog.domain.vo.LinkVO;
import com.blog.service.LinkService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 友情链接管理控制器
 *
 * @author haibara
 * @description 友链相关接口
 * @since 2025/8/1 11:52
 */

@Tag(name = "友链相关接口")
@RestController
@Validated
@RequestMapping("link")
public class LinkController {


    @Resource
    private LinkService linkService;

    /**
     * 申请友链
     * <p>
     * 用户提交友链申请，系统会保存友链信息并根据配置决定是否发送邮件通知站长。
     * 该接口需要用户登录，并会检查用户是否在黑名单中。
     *
     * @param linkDTO 友链申请信息，包含网站名称、地址、描述、背景图等
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LinkDTO 友链申请数据传输对象
     */
    @CheckBlacklist
    @Operation(summary = "申请友链")
    @Parameter(name = "linkDTO", description = "友链申请信息")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.APPLY_LINK_MAX_COUNT)
    @PostMapping("/auth/apply")
    public ResponseResult<Void> applyLink(@RequestBody @Valid LinkDTO linkDTO) {
        return linkService.applyLink(linkDTO);
    }

    /**
     * 查询所有通过申请的友链
     * <p>
     * 获取所有已通过审核的友链列表，用于前台页面展示。
     * 返回的友链信息包含网站名称、地址、描述、背景图和用户头像等。
     *
     * @return 响应结果，包含通过审核的友链列表
     *         <ul>
     *             <li>成功时返回友链列表，每个友链包含基本信息和用户头像</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LinkVO 友链信息视图对象
     */
    @Operation(summary = "查询所有通过申请的友链")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/list")
    public ResponseResult<List<LinkVO>> getLinkList() {
        return ControllerUtils.messageHandler(() -> linkService.getLinkList());
    }

    /**
     * 后台友链列表
     * <p>
     * 获取后台管理系统中的友链列表，包含所有状态的友链信息。
     * 该接口需要管理员权限，用于后台友链管理页面。
     *
     * @return 响应结果，包含后台友链列表
     *         <ul>
     *             <li>成功时返回友链列表，每个友链包含详细信息和用户名</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LinkListVO 后台友链列表视图对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:link:list')")
    @Operation(summary = "后台友链列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "友链管理", operation = LogConst.GET)
    @GetMapping("/back/list")
    public ResponseResult<List<LinkListVO>> backList() {
        return ControllerUtils.messageHandler(() -> linkService.getBackLinkList(null));
    }

    /**
     * 搜索后台友链列表
     * <p>
     * 根据用户名、友链名称、审核状态和时间范围等条件搜索友链列表。
     * 该接口需要管理员权限，支持多条件组合查询。
     *
     * @param searchDTO 搜索条件，包含用户名、友链名称、审核状态、开始时间和结束时间等
     * @return 响应结果，包含符合搜索条件的友链列表
     *         <ul>
     *             <li>成功时返回友链列表，每个友链包含详细信息和用户名</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchLinkDTO 友链搜索条件数据传输对象
     * @see LinkListVO 后台友链列表视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('blog:link:search')")
    @Operation(summary = "搜索后台友链列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "友链管理", operation = LogConst.SEARCH)
    @PostMapping("/back/search")
    public ResponseResult<List<LinkListVO>> backList(@RequestBody SearchLinkDTO searchDTO) {
        return ControllerUtils.messageHandler(() -> linkService.getBackLinkList(searchDTO));
    }

    /**
     * 修改友链审核状态
     * <p>
     * 管理员审核友链申请，可以通过或拒绝友链申请。
     * 当友链通过审核时，系统会根据配置决定是否发送通知邮件给申请者。
     *
     * @param linkIsCheckDTO 友链审核信息，包含友链ID和审核状态
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回操作成功信息，可能包含邮件发送状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LinkIsCheckDTO 友链审核数据传输对象
     * @see LogConst#UPDATE 操作类型：更新
     */
    @PreAuthorize("hasAnyAuthority('blog:link:isCheck')")
    @Operation(summary = "修改友链是否通过")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "友链管理", operation = LogConst.UPDATE)
    @PostMapping("/back/isCheck")
    public ResponseResult<Void> isCheck(@RequestBody @Valid LinkIsCheckDTO linkIsCheckDTO) {
        return linkService.isCheckLink(linkIsCheckDTO);
    }

    /**
     * 删除友链
     * <p>
     * 批量删除指定的友链记录，该操作会同时删除相关的用户评论、点赞、收藏等关联数据。
     * 该接口需要管理员权限，删除操作不可逆。
     *
     * @param ids 要删除的友链ID列表
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回删除成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:link:delete')")
    @Operation(summary = "删除友链")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "友链管理", operation = LogConst.DELETE)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> delete(@RequestBody List<Long> ids) {
        return linkService.deleteLink(ids);
    }

    /**
     * 邮箱审核友链
     * <p>
     * 通过邮件中的验证码链接直接审核友链申请，实现一键审核功能。
     * 站长收到友链申请邮件后，点击邮件中的审核链接即可直接通过友链申请，
     * 无需登录后台管理系统。验证码具有时效性，使用后会自动失效。
     *
     * @param verifyCode 邮件中的验证码，用于验证审核操作的合法性
     * @param response HTTP响应对象，用于返回操作结果页面
     * @return 操作结果页面内容
     *         <ul>
     *             <li>验证码有效且操作成功时返回成功页面</li>
     *             <li>验证码无效或操作失败时返回失败页面</li>
     *         </ul>
     * @see LogConst#APPROVE 操作类型：审核
     */
    @Operation(summary = "邮箱审核友链")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.EMAIL_APPLY_LINK_MAX_COUNT)
    @LogAnnotation(module = "友链审核", operation = LogConst.APPROVE)
    @Parameter(name = "verifyCode", description = "校验码")
    @GetMapping("/email/apply")
    public String emailApply(
            @RequestParam("verifyCode")
            @NotBlank(message = ValidationConstants.VERIFY_CODE_NOT_NULL)
            String verifyCode, HttpServletResponse response) {
        return linkService.emailApplyLink(verifyCode, response);
    }
}
