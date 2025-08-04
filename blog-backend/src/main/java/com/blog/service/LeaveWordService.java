package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.LeaveWordIsCheckDTO;
import com.blog.domain.dto.SearchLeaveWordDTO;
import com.blog.domain.entity.LeaveWord;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LeaveWordListVO;
import com.blog.domain.vo.LeaveWordVO;

import java.util.List;

/**
 * @author haibara
 * @description 留言服务接口
 * @since 2025/7/27
 */
public interface LeaveWordService extends IService<LeaveWord> {

    /**
     * 查询留言板
     * @param id 留言板id
     * @return 留言板列表
     */
    List<LeaveWordVO> getLeaveWordList(String id);

    /**
     * 添加留言板
     *
     * @param content 留言内容
     * @return 是否成功
     */
    ResponseResult<Void> userLeaveWord(String content);

    /**
     * 后台留言列表
     * @param searchDTO 查询dto
     * @return 结果
     */
    List<LeaveWordListVO> getBackLeaveWordList(SearchLeaveWordDTO searchDTO);

    /**
     * 是否通过留言
     * @param isCheckDTO 是否通过
     * @return 是否成功
     */
    ResponseResult<Void> isCheckLeaveWord(LeaveWordIsCheckDTO isCheckDTO);

    /**
     * 删除留言
     * @param ids id 列表
     * @return 是否成功
     */
    ResponseResult<Void> deleteLeaveWord(List<Long> ids);
}
