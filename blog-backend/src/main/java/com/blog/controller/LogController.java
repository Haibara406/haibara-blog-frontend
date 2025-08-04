package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.LogDTO;
import com.blog.domain.dto.LogDeleteDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PageVO;
import com.blog.service.LogService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志管理控制器

 * @author haibara
 * @description 操作日志相关接口
 * @since 2025/7/28 16:53
 */

@Tag(name = "操作日志相关接口")
@RestController
@RequestMapping("log")
@Validated
public class LogController {


    @Resource
    private LogService logService;

    /**
     * 分页获取所有操作日志列表
     * <p>
     * 分页查询系统中所有的操作日志记录，按操作时间倒序排列显示。
     * 该接口用于操作日志管理页面的初始数据加载，支持分页展示以提高性能。
     * 操作完成后会记录操作日志。
     *
     * @param current 当前页码，必填参数，从1开始
     *               <ul>
     *                   <li>必须大于0</li>
     *                   <li>超出范围时返回空列表</li>
     *               </ul>
     * @param pageSize 每页记录数，必填参数
     *                <ul>
     *                    <li>建议范围：10-100</li>
     *                    <li>过大的值可能影响性能</li>
     *                </ul>
     * @return 响应结果，包含分页的操作日志数据
     *         <ul>
     *             <li>成功时返回分页对象，包含：
     *                 <ul>
     *                     <li>records: 当前页的日志记录列表</li>
     *                     <li>total: 总记录数</li>
     *                     <li>current: 当前页码</li>
     *                     <li>size: 每页大小</li>
     *                 </ul>
     *             </li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PageVO 分页视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:log:list')")
    @Operation(summary = "显示所有操作日志")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/list/{current}/{pageSize}")
    public ResponseResult<PageVO> getLogList(@PathVariable("current") @NotNull Long current, @PathVariable("pageSize") @NotNull Long pageSize) {
        return ControllerUtils.messageHandler(() -> logService.searchLog(null, current,pageSize));
    }

    /**
     * 根据条件搜索操作日志
     * <p>
     * 根据指定的搜索条件分页筛选操作日志记录，支持多种条件组合查询。
     * 该接口用于操作日志管理页面的高级搜索功能，帮助管理员快速定位特定的操作记录。
     * 操作完成后会记录操作日志。
     *
     * @param logDTO 搜索条件数据传输对象，包含：
     *              <ul>
     *                  <li>module: 操作模块筛选（如"用户管理"、"角色管理"等）</li>
     *                  <li>operation: 操作类型筛选（如"新增"、"修改"、"删除"等）</li>
     *                  <li>username: 操作人用户名关键词搜索</li>
     *                  <li>operationTimeStart: 操作时间范围开始</li>
     *                  <li>operationTimeEnd: 操作时间范围结束</li>
     *                  <li>ipAddress: 操作IP地址筛选</li>
     *                  <li>current: 当前页码，必填</li>
     *                  <li>pageSize: 每页大小，必填</li>
     *              </ul>
     * @return 响应结果，包含符合条件的分页操作日志数据
     *         <ul>
     *             <li>成功时返回分页对象，包含筛选后的日志记录</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LogDTO 操作日志搜索条件数据传输对象
     * @see PageVO 分页视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:log:search')")
    @Operation(summary = "搜索操作日志")
    @Parameter(name = "loginLogDTO", description = "搜索条件")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @PostMapping("/search")
    public ResponseResult<PageVO> getLogSearch(@RequestBody @Valid LogDTO logDTO) {
        return ControllerUtils.messageHandler(() -> logService.searchLog(logDTO, logDTO.getCurrent(),logDTO.getPageSize()));
    }

    /**
     * 批量删除或清空操作日志
     * <p>
     * 根据提供的条件批量删除操作日志记录，支持按ID列表删除或清空所有记录。
     * 该操作用于定期清理历史操作日志，释放存储空间，但会影响审计追溯能力，请谨慎操作。
     * 操作完成后会记录操作日志。
     *
     * @param logDeleteDTO 删除条件数据传输对象，包含：
     *                    <ul>
     *                        <li>ids: 要删除的操作日志ID列表，可选</li>
     *                        <li>deleteAll: 是否清空所有记录的标志，可选</li>
     *                        <li>beforeDate: 删除指定日期之前的记录，可选</li>
     *                        <li>modules: 按模块删除，可选</li>
     *                    </ul>
     *                    支持多种删除策略：
     *                    <ul>
     *                        <li>按ID列表删除：提供ids参数</li>
     *                        <li>清空所有记录：设置deleteAll为true</li>
     *                        <li>按时间范围删除：提供beforeDate参数</li>
     *                        <li>按模块删除：提供modules参数</li>
     *                    </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，包含删除的记录数量</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LogDeleteDTO 操作日志删除条件数据传输对象
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('system:log:delete')")
    @Operation(summary = "删除/清空操作日志")
    @Parameter(name = "deleteLoginLogDTO", description = "删除的id数组")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "操作日志", operation = LogConst.DELETE)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deleteLog(@RequestBody @Valid LogDeleteDTO logDeleteDTO) {
        return logService.deleteLog(logDeleteDTO);
    }


}
