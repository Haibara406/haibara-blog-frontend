package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.LogDTO;
import com.blog.domain.dto.LogDeleteDTO;
import com.blog.domain.entity.Log;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PageVO;

/**
 * @author haibara
 * @description 系统操作日志服务接口
 * @since 2025/7/27
 */
public interface LogService extends IService<Log> {

    /**
     * 搜索/显示操作日志
     *
     * @param logDTO  查询条件
     * @param current 当前页
     * @param pageSize 每页数量
     * @return 数据列表
     */
    PageVO searchLog(LogDTO logDTO, Long current, Long pageSize);

    /**
     *  删除/清空操作日志
     *
     * @param logDeleteDTO id集合
     * @return  响应结果
     */
    ResponseResult<Void> deleteLog(LogDeleteDTO logDeleteDTO);
}
