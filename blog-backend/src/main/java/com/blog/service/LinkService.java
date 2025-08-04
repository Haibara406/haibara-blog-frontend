package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.LinkDTO;
import com.blog.domain.dto.LinkIsCheckDTO;
import com.blog.domain.dto.SearchLinkDTO;
import com.blog.domain.entity.Link;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LinkListVO;
import com.blog.domain.vo.LinkVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author haibara
 * @description 友情链接服务接口
 * @since 2025/7/27
 */
public interface LinkService extends IService<Link> {


    /**
     * 申请友链
     * @param linkDTO 友链信息
     * @return 是否成功
     */
    ResponseResult<Void> applyLink(LinkDTO linkDTO);

    /**
     * 查询通过审核的友链
     */
    List<LinkVO> getLinkList();


    /**
     * 后台友链列表
     * @return 结果
     */
    List<LinkListVO> getBackLinkList(SearchLinkDTO searchDTO);

    /**
     * 是否通过友链
     * @param isCheckDTO 是否通过
     * @return 是否成功
     */
    ResponseResult<Void> isCheckLink(LinkIsCheckDTO isCheckDTO);

    /**
     * 删除友链
     * @param ids id 列表
     * @return 是否成功
     */
    ResponseResult<Void> deleteLink(List<Long> ids);

    /**
     * 邮箱审核友链申请
     * @param verifyCode 校验码
     * @param response HTTP响应对象，用于返回操作结果页面
     * @return 是否成功
     */
    String emailApplyLink(String verifyCode, HttpServletResponse response);
}
