package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.PopupDTO;
import com.blog.domain.dto.PopupQueryDTO;
import com.blog.domain.entity.Popup;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PageVO;
import com.blog.domain.vo.PopupVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 弹窗管理服务接口
 * <p>
 * 提供弹窗相关的业务逻辑处理，包括弹窗的增删改查、搜索、状态管理等功能。
 * 支持前台弹窗展示和后台管理功能。
 *
 * @author haibara
 * @description 弹窗管理服务接口
 * @since 2025/8/7
 */
public interface PopupService extends IService<Popup> {

    // ==================== 前台接口方法 ====================

    /**
     * 获取当前页面应该显示的弹窗列表
     * <p>
     * 根据页面路径和会话信息，获取符合条件的弹窗列表。
     * 会进行时间段、用户类型、显示频率等多重过滤，并按优先级排序。
     *
     * @param currentPage 当前页面路径
     * @param sessionId 会话ID
     * @return 符合条件的弹窗列表
     */
    List<PopupVO> getCurrentPagePopups(String currentPage, String sessionId);



    // ==================== 后台管理接口方法 ====================

    /**
     * 分页获取弹窗列表
     * <p>
     * 获取后台管理的弹窗列表，支持分页显示。
     * 返回的数据包含弹窗的基本信息和状态描述。
     *
     * @param current 页码
     * @param pageSize 每页数量
     * @return 分页弹窗列表
     */
    PageVO<List<PopupVO>> getPopupList(Long current, Long pageSize);

    /**
     * 搜索弹窗列表
     * <p>
     * 根据搜索条件查询弹窗列表，支持多条件组合搜索和分页。
     * 支持按标题、类型、位置、状态等条件进行筛选。
     *
     * @param popupQueryDTO 搜索条件
     * @return 搜索结果分页列表
     */
    PageVO<List<PopupVO>> searchPopup(PopupQueryDTO popupQueryDTO);

    /**
     * 添加弹窗
     * <p>
     * 创建新的弹窗记录，包含完整的弹窗配置信息。
     * 会进行数据验证和业务规则检查。
     *
     * @param popupDTO 弹窗数据传输对象
     * @return 操作结果
     */
    ResponseResult<Void> addPopup(PopupDTO popupDTO);

    /**
     * 修改弹窗
     * <p>
     * 更新现有弹窗的配置信息。
     * 会验证弹窗是否存在以及数据的有效性。
     *
     * @param popupDTO 弹窗数据传输对象
     * @return 操作结果
     */
    ResponseResult<Void> updatePopup(PopupDTO popupDTO);

    /**
     * 获取弹窗详情
     * <p>
     * 根据弹窗ID获取完整的弹窗信息，用于编辑页面数据回显。
     *
     * @param id 弹窗ID
     * @return 弹窗详情数据
     */
    PopupDTO getPopupById(Long id);

    /**
     * 批量删除弹窗
     * <p>
     * 根据弹窗ID列表批量删除弹窗（逻辑删除）。
     * 会检查弹窗是否存在以及是否可以删除。
     *
     * @param ids 弹窗ID列表
     * @return 操作结果
     */
    ResponseResult<Void> deletePopup(List<Long> ids);

    /**
     * 更新弹窗状态
     * <p>
     * 启用或禁用指定的弹窗。
     * 禁用后的弹窗不会在前台显示。
     *
     * @param id 弹窗ID
     * @param status 状态（0-禁用，1-启用）
     * @return 操作结果
     */
    ResponseResult<Void> updateStatus(Long id, Integer status);

    /**
     * 上传弹窗图片
     * <p>
     * 上传弹窗展示用的图片文件。
     * 支持常见的图片格式，会进行文件大小和格式验证。
     *
     * @param file 图片文件
     * @return 上传结果，包含图片URL
     */
    ResponseResult<String> uploadImage(MultipartFile file);
}
