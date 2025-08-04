package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.AddBlackListDTO;
import com.blog.domain.dto.SearchBlackListDTO;
import com.blog.domain.dto.UpdateBlackListDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.BlackListVO;
import com.blog.exceptions.BlackListException;
import com.blog.service.BlackListService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 黑名单管理控制器
 *
 * @author haibara
 * @description 黑名单相关接口
 * @since 2025/7/28 16:52
 */
@Tag(name = "黑名单相关接口")
@RestController
@RequestMapping("blackList")
@Validated
public class BlackListController {


    @Resource
    private BlackListService blackListService;

    /**
     * 添加黑名单记录
     * <p>
     * 将指定的用户或IP地址添加到黑名单中，支持设置封禁原因和过期时间。
     * 黑名单记录生效后，被封禁的用户或IP将无法正常访问系统功能。
     * 操作完成后会记录操作日志。
     *
     * @param addBlackListDTO 添加黑名单数据传输对象，包含：
     *                       <ul>
     *                           <li>userId: 要封禁的用户ID（与IP地址二选一）</li>
     *                           <li>ipAddress: 要封禁的IP地址（与用户ID二选一）</li>
     *                           <li>reason: 封禁原因，必填</li>
     *                           <li>expiresTime: 过期时间，可选（为空则永久封禁）</li>
     *                       </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @throws BlackListException 当黑名单操作出现业务异常时抛出，如重复添加、参数无效等
     * @see AddBlackListDTO 添加黑名单数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @PreAuthorize("hasAnyAuthority('blog:black:add')")
    @Operation(summary = "添加黑名单")
    @Parameter(name = "addBlackListDTO", description = "添加黑名单DTO")
    @LogAnnotation(module = "黑名单管理", operation = LogConst.INSERT)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/add")
    public ResponseResult<Void> addBlackList(@RequestBody @Valid AddBlackListDTO addBlackListDTO) throws BlackListException {
        return blackListService.addBlackList(addBlackListDTO);
    }

    /**
     * 修改黑名单记录
     * <p>
     * 更新已存在的黑名单记录信息，支持修改封禁原因、过期时间等属性。
     * 该操作通常用于调整封禁策略或延长/缩短封禁时间。
     * 操作完成后会记录操作日志。
     *
     * @param updateBlackListDTO 修改黑名单数据传输对象，包含：
     *                          <ul>
     *                              <li>id: 黑名单记录ID，必填</li>
     *                              <li>reason: 新的封禁原因，可选</li>
     *                              <li>expiresTime: 新的过期时间，可选</li>
     *                          </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是记录不存在或权限不足</li>
     *         </ul>
     * @see UpdateBlackListDTO 修改黑名单数据传输对象
     * @see LogConst#UPDATE 操作类型：更新
     */
    @PreAuthorize("hasAnyAuthority('blog:black:update')")
    @Operation(summary = "修改黑名单")
    @Parameter(name = "updateBlackListDTO", description = "修改黑名单")
    @LogAnnotation(module = "黑名单管理", operation = LogConst.UPDATE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PutMapping("/update")
    public ResponseResult<Void> updateBlackList(@RequestBody @Valid UpdateBlackListDTO updateBlackListDTO) {
        return blackListService.updateBlackList(updateBlackListDTO);
    }

    /**
     * 查询黑名单列表
     * <p>
     * 根据指定条件查询黑名单记录，支持多种筛选条件组合查询。
     * 该接口用于黑名单管理页面展示和搜索功能。
     * 操作完成后会记录操作日志。
     *
     * @param searchBlackListDTO 搜索条件数据传输对象，可选参数，包含：
     *                          <ul>
     *                              <li>userId: 用户ID筛选，可选</li>
     *                              <li>ipAddress: IP地址筛选，可选</li>
     *                              <li>reason: 封禁原因关键词筛选，可选</li>
     *                              <li>status: 状态筛选（有效/过期），可选</li>
     *                              <li>createTimeStart: 创建时间范围开始，可选</li>
     *                              <li>createTimeEnd: 创建时间范围结束，可选</li>
     *                          </ul>
     *                          当参数为null时，返回所有黑名单记录
     * @return 响应结果，包含黑名单记录列表
     *         <ul>
     *             <li>成功时返回黑名单列表，每条记录包含ID、用户信息、IP信息、封禁原因、过期时间、创建时间等</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see SearchBlackListDTO 搜索黑名单条件数据传输对象
     * @see BlackListVO 黑名单视图对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:black:select')")
    @Operation(summary = "查询黑名单")
    @LogAnnotation(module = "黑名单管理", operation = LogConst.GET)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/getBlackListing")
    public ResponseResult<List<BlackListVO>> getBlackList(@RequestBody(required = false) SearchBlackListDTO searchBlackListDTO) {
        return ControllerUtils.messageHandler(() -> blackListService.getBlackList(searchBlackListDTO));
    }

    /**
     * 批量删除黑名单记录
     * <p>
     * 根据提供的ID列表批量删除黑名单记录，支持同时删除多条记录。
     * 删除后，对应的用户或IP地址将恢复正常访问权限。
     * 操作完成后会记录操作日志。
     *
     * @param ids 要删除的黑名单记录ID列表，不能为空
     *            <ul>
     *                <li>支持单个或多个ID</li>
     *                <li>ID必须是有效的黑名单记录ID</li>
     *                <li>不存在的ID会被忽略</li>
     *            </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，表示删除操作完成</li>
     *             <li>失败时返回错误信息，可能是权限不足或系统异常</li>
     *         </ul>
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('blog:black:delete')")
    @Operation(summary = "删除黑名单")
    @Parameter(name = "id", description = "id")
    @LogAnnotation(module = "黑名单管理", operation = LogConst.DELETE)
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deleteBlackList(@RequestBody List<Long> ids) {
        return blackListService.deleteBlackList(ids);
    }
}
