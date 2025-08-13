package com.blog.export.strategy.data;

import com.blog.domain.vo.LoginLogVO;
import com.blog.export.enums.BusinessType;
import com.blog.export.strategy.DataFetchStrategy;
import com.blog.service.LoginLogService;
import com.blog.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 登录日志数据获取策略
 * 
 * @author haibara
 * @description 从登录日志服务获取登录日志列表数据
 * @since 2025/1/21
 */
@Component
public class LoginLogDataFetchStrategy implements DataFetchStrategy<LoginLogVO> {
    
    @Resource
    private LoginLogService loginLogService;
    
    @Override
    public List<LoginLogVO> fetchData() {
        // 调用LoginLogService的searchLoginLog方法获取登录日志列表
        // 限制导出数量为最新的1000条记录，避免数据量过大
        List<LoginLogVO> allLogs = loginLogService.searchLoginLog(null);
        
        // 由于数据已经按照创建时间倒序排列，直接取前1000条
        return allLogs.size() > 1000 ? allLogs.subList(0, 1000) : allLogs;
    }
    
    @Override
    public BusinessType getBusinessType() {
        return BusinessType.LOGIN_LOG;
    }
    
    @Override
    public Class<LoginLogVO> getDataClass() {
        return LoginLogVO.class;
    }
    
    @Override
    public boolean hasPermission() {
        // 检查是否有登录日志查看权限
        return SecurityUtils.hasAnyAuthority("system:log:login:list");
    }
}