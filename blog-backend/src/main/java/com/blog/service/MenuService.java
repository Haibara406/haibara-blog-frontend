package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.MenuDTO;
import com.blog.domain.entity.Menu;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.MenuByIdVO;
import com.blog.domain.vo.MenuVO;

import java.util.List;

/**
 * @author haibara
 * @description 系统菜单服务接口
 * @since 2025/7/27
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取菜单列表,0:系统菜单，1:菜单管理
     *
     * @param typeId 菜单类型id
     * @param title 菜单标题
     * @param status 状态
     * @return 菜单列表
     */
    ResponseResult<List<MenuVO>> getMenuList(Integer typeId, String title, Integer status);

    /**
     * 添加菜单
     *
     * @param menuDTO 菜单信息
     * @return 添加菜单的结果
     */
    ResponseResult<Void> addMenu(MenuDTO menuDTO);

    /**
     * 修改菜单，id回滚
     *
     * @param id 菜单id
     * @return 数据
     */
    ResponseResult<MenuByIdVO> selectUpdateMenu(Long id);

    /**
     * 修改菜单
     *
     * @param menuDTO 菜单信息
     * @return 是否成功
     */
    ResponseResult<Void> updateMenu(MenuDTO menuDTO);

    /**
     * 根据id删除菜单
     *
     * @param id 对应的id
     * @return 是否成功
     */
    ResponseResult<String> deleteMenu(Long id);
}
