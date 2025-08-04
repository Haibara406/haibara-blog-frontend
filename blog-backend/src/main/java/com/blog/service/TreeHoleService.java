package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.SearchTreeHoleDTO;
import com.blog.domain.dto.TreeHoleIsCheckDTO;
import com.blog.domain.entity.TreeHole;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.TreeHoleListVO;
import com.blog.domain.vo.TreeHoleVO;

import java.util.List;

/**
 * @author haibara
 * @description 树洞服务接口
 * @since 2025/7/27
 */
public interface TreeHoleService extends IService<TreeHole> {

    /**
     * 新增树洞
     * @param content 树洞内容
     * @return 是否成功
     */
    ResponseResult<Void> addTreeHole(String content);
    /**
     * 查看树洞
     * @return 树洞列表
     */
    List<TreeHoleVO> getTreeHole();
    /**
     * 后台树洞列表
     * @param searchDTO 搜索请求
     * @return 结果
     */
    List<TreeHoleListVO> getBackTreeHoleList(SearchTreeHoleDTO searchDTO);
    /**
     * 是否通过树洞
     * @param isCheckDTO 是否通过
     * @return 是否成功
     */
    ResponseResult<Void> isCheckTreeHole(TreeHoleIsCheckDTO isCheckDTO);
    /**
     * 删除树洞
     * @param ids id 列表
     * @return 是否成功
     */
    ResponseResult<Void> deleteTreeHole(List<Long> ids);
}
