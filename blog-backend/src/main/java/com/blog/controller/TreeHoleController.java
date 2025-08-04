package com.blog.controller;

import com.alibaba.fastjson.JSON;
import com.blog.annotation.AccessLimit;
import com.blog.annotation.CheckBlacklist;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.SearchTreeHoleDTO;
import com.blog.domain.dto.TreeHoleIsCheckDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.TreeHoleListVO;
import com.blog.domain.vo.TreeHoleVO;
import com.blog.service.TreeHoleService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
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
 * @description 树洞相关接口
 * @since 2025/7/31 11:48
 */

@RestController
@Tag(name = "树洞相关接口")
@RequestMapping("/treeHole")
@Validated
public class TreeHoleController {

    @Resource
    private TreeHoleService treeHoleService;

    /**
     * 添加树洞
     * <p>
     * 用户发布新的树洞内容，支持匿名发布。系统会自动检查用户是否在黑名单中，
     * 并对发布频率进行限制以防止恶意刷屏。发布的内容需要经过审核才能公开显示。
     *
     * @param content JSON格式的请求体，包含：
     *               <ul>
     *                   <li>content: 树洞内容，不能为空，用于发布的文本内容</li>
     *               </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示树洞已成功提交审核</li>
     *             <li>失败时返回错误信息，可能是因为内容为空、用户在黑名单中或发布过于频繁</li>
     *         </ul>
     * @see CheckBlacklist 黑名单检查注解
     */
    @CheckBlacklist
    @Operation(summary = "添加树洞")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.ADD_TREE_HOLE_MAX_COUNT)
    @PostMapping("/auth/addTreeHole")
    public ResponseResult<Void> addTreeHole(@Valid @NotNull @RequestBody String content) {
        return treeHoleService.addTreeHole(JSON.parseObject(content).getString("content"));
    }

    /**
     * 查看树洞列表
     * <p>
     * 获取所有已审核通过的树洞内容列表，按发布时间倒序排列。
     * 该接口对所有用户开放，无需登录即可访问，但有访问频率限制。
     * 返回的树洞内容已脱敏，不包含用户身份信息。
     *
     * @return 响应结果，包含树洞列表
     *         <ul>
     *             <li>成功时返回树洞列表，每个树洞包含ID、内容、发布时间等信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TreeHoleVO 树洞信息视图对象
     */
    @Operation(summary = "查看树洞")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.GET_TREE_HOLE_LIST_MAX_COUNT)
    @GetMapping("/getTreeHoleList")
    public ResponseResult<List<TreeHoleVO>> getTreeHoleList() {
        return ControllerUtils.messageHandler(() -> treeHoleService.getTreeHole());
    }

    /**
     * 后台树洞列表
     * <p>
     * 获取后台管理系统中的所有树洞列表，包括已审核和未审核的内容。
     * 该接口仅限具有树洞列表查看权限的管理员访问，用于树洞内容的管理和审核。
     * 操作会被记录到系统日志中。
     *
     * @return 响应结果，包含后台树洞列表
     *         <ul>
     *             <li>成功时返回树洞列表，每个树洞包含ID、内容、审核状态、发布时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see TreeHoleListVO 后台树洞列表视图对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:treeHole:list')")
    @Operation(summary = "后台树洞列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="树洞管理",operation= LogConst.GET)
    @GetMapping("/back/list")
    public ResponseResult<List<TreeHoleListVO>> backList() {
        return ControllerUtils.messageHandler(() -> treeHoleService.getBackTreeHoleList(null));
    }

    /**
     * 搜索后台树洞列表
     * <p>
     * 根据指定条件搜索后台树洞列表，支持按内容、审核状态、发布时间等条件进行筛选。
     * 该接口仅限具有树洞搜索权限的管理员访问，用于快速定位特定的树洞内容。
     * 操作会被记录到系统日志中。
     *
     * @param searchDTO 搜索条件数据传输对象，包含：
     *                 <ul>
     *                     <li>content: 树洞内容关键词，可选参数，用于模糊匹配树洞内容</li>
     *                     <li>isCheck: 审核状态，可选参数，用于筛选已审核或未审核的树洞</li>
     *                     <li>startTime: 开始时间，可选参数，用于筛选指定时间范围的树洞</li>
     *                     <li>endTime: 结束时间，可选参数，用于筛选指定时间范围的树洞</li>
     *                 </ul>
     * @return 响应结果，包含符合搜索条件的树洞列表
     *         <ul>
     *             <li>成功时返回树洞列表，每个树洞包含ID、内容、审核状态、发布时间等管理信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchTreeHoleDTO 树洞搜索条件数据传输对象
     * @see TreeHoleListVO 后台树洞列表视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('blog:treeHole:search')")
    @Operation(summary = "搜索后台树洞列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="树洞管理",operation= LogConst.SEARCH)
    @PostMapping("/back/search")
    public ResponseResult<List<TreeHoleListVO>> backList(@RequestBody SearchTreeHoleDTO searchDTO) {
        return ControllerUtils.messageHandler(() -> treeHoleService.getBackTreeHoleList(searchDTO));
    }

    /**
     * 修改树洞审核状态
     * <p>
     * 管理员审核树洞内容，设置树洞是否通过审核。通过审核的树洞将在前台显示，
     * 未通过审核的树洞将被隐藏。该操作仅限具有树洞审核权限的管理员执行，
     * 操作会被记录到系统日志中。
     *
     * @param treeHoleIsCheckDTO 树洞审核状态数据传输对象，包含：
     *                          <ul>
     *                              <li>id: 树洞ID，不能为空，用于指定要审核的树洞</li>
     *                              <li>isCheck: 审核状态，不能为空，true表示通过审核，false表示不通过</li>
     *                          </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示审核状态已成功更新</li>
     *             <li>失败时返回错误信息，可能是因为树洞不存在或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当必填字段为空时抛出
     * @see TreeHoleIsCheckDTO 树洞审核状态数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @PreAuthorize("hasAnyAuthority('blog:treeHole:isCheck')")
    @Operation(summary = "修改树洞是否通过")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="树洞管理",operation= LogConst.UPDATE)
    @PostMapping("/back/isCheck")
    public ResponseResult<Void> isCheck(@RequestBody @Valid TreeHoleIsCheckDTO treeHoleIsCheckDTO) {
        return treeHoleService.isCheckTreeHole(treeHoleIsCheckDTO);
    }

    /**
     * 删除树洞
     * <p>
     * 批量删除指定的树洞内容，该操作将永久删除树洞数据，不可恢复。
     * 该操作仅限具有树洞删除权限的管理员执行，操作会被记录到系统日志中。
     * 建议在删除前确认树洞内容确实需要删除。
     *
     * @param ids 树洞ID列表，不能为空，用于指定要删除的树洞
     *           <ul>
     *               <li>支持批量删除，可传入多个树洞ID</li>
     *               <li>如果某个ID对应的树洞不存在，将跳过该ID继续处理其他ID</li>
     *           </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示指定的树洞已成功删除</li>
     *             <li>失败时返回错误信息，可能是因为参数为空或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当树洞ID列表为空时抛出
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:treeHole:delete')")
    @Operation(summary = "删除树洞")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="树洞管理",operation= LogConst.DELETE)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> delete(@RequestBody List<Long> ids) {
        return treeHoleService.deleteTreeHole(ids);
    }
}
