package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.CheckBlacklist;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.LeaveWordIsCheckDTO;
import com.blog.domain.dto.SearchLeaveWordDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LeaveWordListVO;
import com.blog.domain.vo.LeaveWordVO;
import com.blog.service.LeaveWordService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
 * @description 留言板相关接口
 * @since 2025/7/31 12:22
 */

@RestController
@RequestMapping("leaveWord")
@Validated
@Tag(name = "留言板", description = "留言板相关接口")
public class LeaveWordController {

    @Resource
    private LeaveWordService leaveWordService;

    /**
     * 获取留言板列表
     * <p>
     * 获取前台展示的留言板内容列表，支持按留言ID进行筛选。
     * 该接口对所有用户开放，无需登录即可访问，但有访问频率限制。
     * 返回的留言内容已经过审核，按发布时间倒序排列，支持嵌套回复结构。
     *
     * @param id 留言板ID，可选参数，用于获取特定留言的详细信息或回复列表
     *          <ul>
     *              <li>为空时：返回所有已审核通过的留言列表</li>
     *              <li>不为空时：返回指定留言的详细信息及其回复</li>
     *          </ul>
     * @return 响应结果，包含留言列表
     *         <ul>
     *             <li>成功时返回留言列表，每个留言包含ID、内容、用户信息、发布时间、回复列表等</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LeaveWordVO 留言信息视图对象
     */
    @Operation(summary = "获取留言板列表")
    @Parameters({
            @Parameter(name = "id", description = "留言板id", in = ParameterIn.QUERY)
    })
    @GetMapping("/list")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_LEAVE_WORD_LIST_MAX_COUNT)
    public ResponseResult<List<LeaveWordVO>> list(@RequestParam(value = "id",required = false) String id) {
        return ControllerUtils.messageHandler(() -> leaveWordService.getLeaveWordList(id));
    }

    /**
     * 用户留言
     * <p>
     * 用户发布新的留言内容到留言板，支持文本内容和表情符号。
     * 系统会自动检查用户是否在黑名单中，并对留言频率进行限制以防止恶意刷屏。
     * 发布的留言需要经过管理员审核才能在前台显示。
     * 该接口需要用户登录后才能访问。
     *
     * @param content JSON格式的请求体，包含留言内容，不能为空
     *               <ul>
     *                   <li>支持纯文本内容</li>
     *                   <li>支持表情符号和特殊字符</li>
     *                   <li>内容长度有限制，具体限制由前端和后端验证控制</li>
     *               </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示留言已成功提交审核</li>
     *             <li>失败时返回错误信息，可能是因为内容为空、用户在黑名单中或留言过于频繁</li>
     *         </ul>
     * @see CheckBlacklist 黑名单检查注解
     */
    @CheckBlacklist
    @Operation(summary = "用户留言")
    @PostMapping("/auth/userLeaveWord")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.USER_LEAVE_WORD_MAX_COUNT)
    public ResponseResult<Void> userLeaveWord(@RequestBody @NotNull String content) {
        return leaveWordService.userLeaveWord(content);
    }

    /**
     * 后台留言列表
     * <p>
     * 获取后台管理系统中的所有留言列表，包括已审核和未审核的内容。
     * 该接口仅限具有留言列表查看权限的管理员访问，用于留言内容的管理和审核。
     * 返回的数据包含留言的完整管理信息，操作会被记录到系统日志中。
     *
     * @return 响应结果，包含后台留言列表
     *         <ul>
     *             <li>成功时返回留言列表，每个留言包含ID、内容、用户信息、审核状态、发布时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LeaveWordListVO 后台留言列表视图对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:leaveword:list')")
    @Operation(summary = "后台留言列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="留言管理",operation= LogConst.GET)
    @GetMapping("/back/list")
    public ResponseResult<List<LeaveWordListVO>> backList() {
        return ControllerUtils.messageHandler(() -> leaveWordService.getBackLeaveWordList(null));
    }

    /**
     * 搜索后台留言列表
     * <p>
     * 根据指定条件搜索后台留言列表，支持按内容、审核状态、用户信息、发布时间等条件进行筛选。
     * 该接口仅限具有留言搜索权限的管理员访问，用于快速定位特定的留言内容。
     * 操作会被记录到系统日志中。
     *
     * @param searchDTO 搜索条件数据传输对象，包含：
     *                 <ul>
     *                     <li>content: 留言内容关键词，可选参数，用于模糊匹配留言内容</li>
     *                     <li>isCheck: 审核状态，可选参数，用于筛选已审核或未审核的留言</li>
     *                     <li>username: 用户名，可选参数，用于筛选特定用户的留言</li>
     *                     <li>startTime: 开始时间，可选参数，用于筛选指定时间范围的留言</li>
     *                     <li>endTime: 结束时间，可选参数，用于筛选指定时间范围的留言</li>
     *                 </ul>
     * @return 响应结果，包含符合搜索条件的留言列表
     *         <ul>
     *             <li>成功时返回留言列表，每个留言包含ID、内容、用户信息、审核状态、发布时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchLeaveWordDTO 留言搜索条件数据传输对象
     * @see LeaveWordListVO 后台留言列表视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('blog:leaveword:search')")
    @Operation(summary = "搜索后台留言列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="留言管理",operation= LogConst.SEARCH)
    @PostMapping("/back/search")
    public ResponseResult<List<LeaveWordListVO>> backList(@RequestBody SearchLeaveWordDTO searchDTO) {
        return ControllerUtils.messageHandler(() -> leaveWordService.getBackLeaveWordList(searchDTO));
    }

    /**
     * 修改留言审核状态
     * <p>
     * 管理员审核留言内容，设置留言是否通过审核。通过审核的留言将在前台留言板显示，
     * 未通过审核的留言将被隐藏。该操作仅限具有留言审核权限的管理员执行，
     * 操作会被记录到系统日志中。
     *
     * @param leaveWordIsCheckDTO 留言审核状态数据传输对象，包含：
     *                           <ul>
     *                               <li>id: 留言ID，不能为空，用于指定要审核的留言</li>
     *                               <li>isCheck: 审核状态，不能为空，true表示通过审核，false表示不通过</li>
     *                           </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示审核状态已成功更新</li>
     *             <li>失败时返回错误信息，可能是因为留言不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当必填字段为空时抛出
     * @see LeaveWordIsCheckDTO 留言审核状态数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @PreAuthorize("hasAnyAuthority('blog:leaveword:isCheck')")
    @Operation(summary = "修改留言是否通过")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="留言管理",operation= LogConst.UPDATE)
    @PostMapping("/back/isCheck")
    public ResponseResult<Void> isCheck(@RequestBody @Valid LeaveWordIsCheckDTO leaveWordIsCheckDTO) {
        return leaveWordService.isCheckLeaveWord(leaveWordIsCheckDTO);
    }

    /**
     * 删除留言
     * <p>
     * 批量删除指定的留言内容，该操作将永久删除留言数据，不可恢复。
     * 删除留言时会同时删除该留言下的所有回复内容。
     * 该操作仅限具有留言删除权限的管理员执行，操作会被记录到系统日志中。
     * 建议在删除前确认留言内容确实需要删除。
     *
     * @param ids 留言ID列表，不能为空，用于指定要删除的留言
     *           <ul>
     *               <li>支持批量删除，可传入多个留言ID</li>
     *               <li>如果某个ID对应的留言不存在，将跳过该ID继续处理其他ID</li>
     *               <li>删除留言会级联删除其所有回复</li>
     *           </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示指定的留言已成功删除</li>
     *             <li>失败时返回错误信息，可能是因为参数为空或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当留言ID列表为空时抛出
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:leaveword:delete')")
    @Operation(summary = "删除留言")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="留言管理",operation= LogConst.DELETE)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> delete(@RequestBody List<Long> ids) {
        return leaveWordService.deleteLeaveWord(ids);
    }

}
