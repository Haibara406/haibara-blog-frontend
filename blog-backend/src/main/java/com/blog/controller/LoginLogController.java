package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.LoginLogDTO;
import com.blog.domain.dto.LoginLogDeleteDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LoginLogVO;
import com.blog.service.LoginLogService;
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
 * 登录日志管理控制器
 *
 * @author haibara
 * @description 登录日志相关接口
 * @since 2025/7/28 15:08
 */

@Tag(name = "登录日志相关接口")
@RestController
@RequestMapping("loginLog")
@Validated
public class LoginLogController {


    @Resource
    private LoginLogService loginLogService;

    /**
     * 获取所有登录日志列表
     * <p>
     * 查询系统中所有的登录日志记录，按时间倒序排列显示。
     * 该接口用于登录日志管理页面的初始数据加载。
     * 操作完成后会记录操作日志。
     *
     * @return 响应结果，包含完整的登录日志列表
     *         <ul>
     *             <li>成功时返回登录日志列表，每条记录包含：
     *                 <ul>
     *                     <li>用户名和用户ID</li>
     *                     <li>登录时间和登录状态（成功/失败）</li>
     *                     <li>登录IP地址和地理位置</li>
     *                     <li>浏览器和操作系统信息</li>
     *                     <li>登录方式（邮箱/第三方）</li>
     *                 </ul>
     *             </li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LoginLogVO 登录日志视图对象
     */
    @PreAuthorize("hasAnyAuthority('system:log:login:list')")
    @Operation(summary = "显示所有登录日志")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/list")
    public ResponseResult<List<LoginLogVO>> getLoginLogList() {
        return ControllerUtils.messageHandler(() -> loginLogService.searchLoginLog(null));
    }

    /**
     * 根据条件搜索登录日志
     * <p>
     * 根据指定的搜索条件筛选登录日志记录，支持多种条件组合查询。
     * 该接口用于登录日志管理页面的高级搜索功能，帮助管理员快速定位特定的登录记录。
     * 操作完成后会记录操作日志。
     *
     * @param loginLogDTO 搜索条件数据传输对象，包含：
     *                   <ul>
     *                       <li>username: 用户名关键词搜索，支持模糊匹配</li>
     *                       <li>loginStatus: 登录状态筛选（0-成功，1-失败）</li>
     *                       <li>ipAddress: IP地址筛选，支持精确匹配</li>
     *                       <li>loginTimeStart: 登录时间范围开始</li>
     *                       <li>loginTimeEnd: 登录时间范围结束</li>
     *                       <li>loginType: 登录方式筛选（邮箱/第三方）</li>
     *                   </ul>
     * @return 响应结果，包含符合条件的登录日志列表
     *         <ul>
     *             <li>成功时返回筛选后的登录日志列表</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LoginLogDTO 登录日志搜索条件数据传输对象
     * @see LoginLogVO 登录日志视图对象
     * @see LogConst#SEARCH 操作类型：搜索
     */
    @PreAuthorize("hasAnyAuthority('system:log:login:search')")
    @Operation(summary = "搜索登录日志")
    @Parameter(name = "loginLogDTO", description = "搜索条件")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="登录日志",operation= LogConst.SEARCH)
    @PostMapping("/search")
    public ResponseResult<List<LoginLogVO>> getLoginLogSearch(@RequestBody LoginLogDTO loginLogDTO) {
        return ControllerUtils.messageHandler(() -> loginLogService.searchLoginLog(loginLogDTO));
    }

    /**
     * 批量删除或清空登录日志
     * <p>
     * 根据提供的条件批量删除登录日志记录，支持按ID列表删除或清空所有记录。
     * 该操作用于定期清理历史登录日志，释放存储空间，但会影响审计追溯能力。
     * 操作完成后会记录操作日志。
     *
     * @param loginLogDeleteDTO 删除条件数据传输对象，包含：
     *                         <ul>
     *                             <li>ids: 要删除的登录日志ID列表，可选</li>
     *                             <li>deleteAll: 是否清空所有记录的标志，可选</li>
     *                             <li>beforeDate: 删除指定日期之前的记录，可选</li>
     *                         </ul>
     *                         当ids不为空时，按ID列表删除；
     *                         当deleteAll为true时，清空所有记录；
     *                         当beforeDate不为空时，删除指定日期之前的记录
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态，包含删除的记录数量</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see LoginLogDeleteDTO 登录日志删除条件数据传输对象
     * @see LogConst#DELETE 操作类型：删除
     */
    @PreAuthorize("hasAnyAuthority('system:log:login:delete')")
    @Operation(summary = "删除/清空登录日志")
    @Parameter(name = "loginLogDeleteDTO", description = "删除的id数组")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module="登录日志",operation= LogConst.DELETE)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deleteLoginLog(@RequestBody @Valid LoginLogDeleteDTO loginLogDeleteDTO) {
        return loginLogService.deleteLoginLog(loginLogDeleteDTO);
    }
}
