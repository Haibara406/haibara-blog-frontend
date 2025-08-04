package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.ErrorConst;
import com.blog.constants.RedisConst;
import com.blog.constants.SQLConst;
import com.blog.constants.SuccessConst;
import com.blog.domain.dto.LinkDTO;
import com.blog.domain.dto.LinkIsCheckDTO;
import com.blog.domain.dto.SearchLinkDTO;
import com.blog.domain.entity.Link;
import com.blog.domain.entity.User;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LinkListVO;
import com.blog.domain.vo.LinkVO;
import com.blog.enums.MailboxAlertsEnum;
import com.blog.mapper.LinkMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.LinkService;
import com.blog.service.PublicService;
import com.blog.utils.RedisCache;
import com.blog.utils.SecurityUtils;
import com.blog.utils.StringUtils;
import com.blog.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

/**
 * @author haibara
 * @description 友情链接服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Resource
    private LinkMapper linkMapper;

    @Resource
    private PublicService publicService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisCache redisCache;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${mail.apply-notice}")
    private Boolean applyNotice;

    @Value("${mail.pass-notice}")
    private Boolean passNotice;


    /**
     * 申请友链
     *
     * @param linkDTO 友链信息
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> applyLink(LinkDTO linkDTO) {
        Link link = linkDTO.asViewObject(Link.class);
        link.setUserId(SecurityUtils.getUserId());
        // 1.数据库添加
        if (this.save(link)) {
            Map<String, Object> content = new HashMap<>();
            content.put("name", link.getName());
            content.put("url", link.getUrl());
            content.put("description", link.getDescription());
            content.put("background", link.getBackground());
            content.put("linkEmail", link.getEmail());
            content.put("linkId", link.getId());

            if (applyNotice) {
                // 2.向站长发送邮件
                publicService.sendEmail(MailboxAlertsEnum.FRIEND_LINK_APPLICATION.getCodeStr(), email, content);
            }
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 查询通过审核的友链
     */
    @Override
    public List<LinkVO> getLinkList() {
        List<Link> links = linkMapper
                .selectList(new LambdaQueryWrapper<Link>()
                        .eq(Link::getIsCheck, SQLConst.STATUS_PUBLIC));

        if (links.isEmpty()) {
            return List.of();
        }

        // 批量查询用户信息
        Set<Long> userIds = links.stream().map(Link::getUserId).collect(Collectors.toSet());
        Map<Long, String> userAvatarMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getAvatar));

        return links.stream()
                .map(link -> link.asViewObject(LinkVO.class,
                    v -> v.setAvatar(userAvatarMap.get(link.getUserId()))))
                .toList();
    }

    /**
     * 后台友链列表
     *
     * @param searchDTO
     * @return 结果
     */
    @Override
    public List<LinkListVO> getBackLinkList(SearchLinkDTO searchDTO) {
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotNull(searchDTO)) {
            // 搜索
            List<User> users = userMapper
                    .selectList(new LambdaQueryWrapper<User>()
                            .like(User::getUsername, searchDTO.getUserName()));

            if (!users.isEmpty())
                wrapper.in(StringUtils.isNotEmpty(searchDTO.getUserName()), Link::getUserId, users.stream().map(User::getId).collect(Collectors.toList()));
            else
                wrapper.eq(StringUtils.isNotNull(searchDTO.getUserName()), Link::getUserId, null);

            wrapper.like(StringUtils.isNotEmpty(searchDTO.getName()), Link::getName, searchDTO.getName())
                    .eq(StringUtils.isNotNull(searchDTO.getIsCheck()), Link::getIsCheck, searchDTO.getIsCheck());
            if (StringUtils.isNotNull(searchDTO.getStartTime()) && StringUtils.isNotNull(searchDTO.getEndTime()))
                wrapper.between(Link::getCreateTime, searchDTO.getStartTime(), searchDTO.getEndTime());
        }
        List<Link> links = linkMapper.selectList(wrapper);
        if (links.isEmpty()) {
            return List.of();
        }

        // 批量查询用户信息
        Set<Long> userIds = links.stream().map(Link::getUserId).collect(Collectors.toSet());
        Map<Long, String> userNameMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        return links
                .stream()
                .map(link -> link.asViewObject(LinkListVO.class,
                    v -> v.setUserName(userNameMap.get(link.getUserId()))))
                .toList();
    }

    /**
     * 是否通过友链
     *
     * @param isCheckDTO 是否通过
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> isCheckLink(LinkIsCheckDTO isCheckDTO) {
        if (linkMapper.updateById(Link.builder().id(isCheckDTO.getId()).isCheck(isCheckDTO.getIsCheck()).build()) > 0) {
            // 修改成功，发送邮件
            if (Objects.equals(isCheckDTO.getIsCheck(), SQLConst.STATUS_PUBLIC)) {
                if (passNotice) {
                    publicService.sendEmail(MailboxAlertsEnum.FRIEND_LINK_APPLICATION_PASS.getCodeStr(), linkMapper.selectById(isCheckDTO.getId()).getEmail(), null);
                    return ResponseResult.success(null, SuccessConst.OPERATION_SUCCESSFUL_WITH_EMAIL);
                }
            }
            return ResponseResult.success(null, SuccessConst.OPERATION_SUCCESSFUL);
        }

        return ResponseResult.failure();
    }

    /**
     * 删除友链
     *
     * @param ids id 列表
     * @return 是否成功
     */
    @Override
    public ResponseResult<Void> deleteLink(List<Long> ids) {
        if (linkMapper.deleteBatchIds(ids) > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 邮箱审核友链申请
     *
     * @param verifyCode 校验码
     * @param response HTTP响应对象，用于返回操作结果页面
     * @return 是否成功
     */
    @Override
    public String emailApplyLink(String verifyCode, HttpServletResponse response) {
        if (redisCache.isHasKey(RedisConst.EMAIL_VERIFICATION_LINK + verifyCode)) {
            // 通过该友链
            String linkIdAndEmail = redisCache.getCacheObject(RedisConst.EMAIL_VERIFICATION_LINK + verifyCode);
            // 空格切分
            String[] split = linkIdAndEmail.split(" ");
            if (linkMapper.updateById(Link.builder().id(Long.valueOf(split[0])).isCheck(SQLConst.IS_CHECK_YES).build()) > 0) {
                // 清除
                redisCache.deleteObject(RedisConst.EMAIL_VERIFICATION_LINK + verifyCode);
                if (passNotice) {
                    // 修改成功，发送邮件
                    publicService.sendEmail(MailboxAlertsEnum.FRIEND_LINK_APPLICATION_PASS.getCodeStr(), split[1], null);
                    return WebUtil.renderString(response, SuccessConst.OPERATION_SUCCESSFUL_WITH_EMAIL);
                }
                return WebUtil.renderString(response, SuccessConst.OPERATION_SUCCESSFUL);
            }
        }
        return WebUtil.renderString(response, ErrorConst.OPERATION_FAILED);
    }
}
