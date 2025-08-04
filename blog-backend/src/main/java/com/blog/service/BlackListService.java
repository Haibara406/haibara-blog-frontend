package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.AddBlackListDTO;
import com.blog.domain.dto.SearchBlackListDTO;
import com.blog.domain.dto.UpdateBlackListDTO;
import com.blog.domain.entity.BlackList;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.BlackListVO;

import java.util.List;

/**
 * @author haibara
 * @description 黑名单服务接口
 * @since 2025/7/27
 */
public interface BlackListService extends IService<BlackList> {

    /**
     * 新增数据
     * @param addBlackListDTO 新增对象
     * @return 新增结果
     */
    ResponseResult<Void> addBlackList(AddBlackListDTO addBlackListDTO);

    /**
     * 获取黑名单
     * @return 黑名单
     */
    List<BlackListVO> getBlackList(SearchBlackListDTO searchBlackListDTO);

    /**
     * 修改数据
     * @param updateBlackListDTO 修改对象
     * @return 修改结果
     */
    ResponseResult<Void> updateBlackList(UpdateBlackListDTO updateBlackListDTO);

    /**
     * 删除黑名单
     * @param ids 黑名单id
     * @return 是否成功
     */
    ResponseResult<Void> deleteBlackList(List<Long> ids);
}
