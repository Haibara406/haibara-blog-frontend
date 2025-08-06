package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.Const;
import com.blog.constants.UserConst;
import com.blog.domain.dto.LoginLogDTO;
import com.blog.domain.dto.LoginLogDeleteDTO;
import com.blog.domain.entity.LoginLog;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.LoginLogVO;
import com.blog.enums.LoginTypeEnum;
import com.blog.mapper.LoginLogMapper;
import com.blog.service.LoginLogService;

import com.blog.utils.BrowserUtil;
import com.blog.utils.IpUtils;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author haibara
 * @description 登录日志服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("loginLogService")
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {


    @Resource
    private LoginLogMapper loginLogMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.routingKey.log-login}")
    private String routingKey;

    @Value("${spring.rabbitmq.exchange.log}")
    private String exchange;



    /**
     * 登录日志记录
     *
     * @param request 请求对象
     * @param userName 用户名称
     * @param state 状态（0成功 1失败）
     * @param message 信息
     */
    @Override
    public void loginLog(HttpServletRequest request, String userName, Integer state, String message) {
        String browserName = BrowserUtil.browserName(request);
        String ipAddress = IpUtils.getIpAddr(request);
        String os = BrowserUtil.osName(request);

        // 获取地址信息
        String address;
        if (IpUtils.internalIp(ipAddress)) {
            // 内网IP直接设置
            address = "内网IP";
        } else {
            // 外网IP先设置为未知，后续通过异步方式更新为真实地址
            address = "未知";
        }

        int requestType;
        String typeHeader = request.getHeader(Const.TYPE_HEADER);
        if (StringUtils.isNotEmpty(typeHeader) && typeHeader.equals(Const.FRONTEND_REQUEST))
            requestType = LoginTypeEnum.FRONTEND.getValue();
        else if (StringUtils.isNotEmpty(typeHeader) && typeHeader.equals(Const.BACKEND_REQUEST)) {
            requestType = LoginTypeEnum.BACKEND.getValue();
        } else {
            requestType = LoginTypeEnum.ILLEGAL.getValue();
        }
        if (userName == null) {
            userName = UserConst.UNKNOWN_USER;
        }
        LoginLog logEntity = LoginLog.builder()
                .userName(userName)
                .ip(ipAddress)
                .address(address)
                .browser(browserName)
                .os(os)
                .type(requestType)
                .state(state)
                .message(message)
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKey, logEntity);
        log.info("{}", "发送登录日志信息--rabbitMQ");
    }

    /**
     * 搜索/显示登录日志
     *
     * @param loginLogDTO 查询条件
     * @return 数据列表
     */
    @Override
    public List<LoginLogVO> searchLoginLog(LoginLogDTO loginLogDTO) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(loginLogDTO)) {
            wrapper.like(StringUtils.isNotEmpty(loginLogDTO.getAddress()), LoginLog::getAddress, loginLogDTO.getAddress())
                    .like(StringUtils.isNotEmpty(loginLogDTO.getUserName()), LoginLog::getUserName, loginLogDTO.getUserName())
                    .eq(StringUtils.isNotNull(loginLogDTO.getState()), LoginLog::getState, loginLogDTO.getState());
            if (StringUtils.isNotNull(loginLogDTO.getLoginTimeStart()) && StringUtils.isNotNull(loginLogDTO.getLoginTimeEnd())) {
                wrapper.gt(LoginLog::getCreateTime, loginLogDTO.getLoginTimeStart())
                        .and(a -> a.lt(LoginLog::getCreateTime, loginLogDTO.getLoginTimeEnd()));
            }
        }
        wrapper.orderByDesc(LoginLog::getCreateTime);
        return loginLogMapper
                .selectList(wrapper)
                .stream()
                .map(loginLog -> loginLog.asViewObject(LoginLogVO.class,v -> v.setLoginTime(loginLog.getCreateTime())))
                .toList();
    }

    /**
     * 删除登录日志
     *
     * @param loginLogDeleteDTO 删除条件
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<Void> deleteLoginLog(LoginLogDeleteDTO loginLogDeleteDTO) {
        if (this.removeByIds(loginLogDeleteDTO.getIds())) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }
}
