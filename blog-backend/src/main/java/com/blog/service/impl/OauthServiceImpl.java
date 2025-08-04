package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.constants.OauthConst;
import com.blog.domain.entity.User;
import com.blog.enums.RegisterOrLoginTypeEnum;
import com.blog.mapper.UserMapper;
import com.blog.service.IpService;
import com.blog.service.OauthService;
import com.blog.service.UserService;
import com.blog.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author haibara
 * @description 第三方登录实现类
 * @since 2025/7/27 15:34
 */
@Slf4j
@Service
public class OauthServiceImpl implements OauthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private IpService ipService;


    /**
     * 处理第三方登录
     *
     * @param authResponse 授权响应
     * @param request      请求
     * @param type         登录类型
     * @return 响应结果
     */
    @Override
    public String handleLogin(AuthResponse authResponse, HttpServletRequest request, Integer type) {
        if (authResponse.getCode() == 2000) {
            AuthUser authUser = (AuthUser) authResponse.getData();
            // 第三方登录默认密码
            String enPassword = passwordEncoder.encode(authUser.getToken().getAccessToken());
            if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getId, authUser.getUuid()).eq(User::getRegisterType, type)) == 0) {
                // 未注册
                String ipAddr = IpUtils.getIpAddr(request);

                User user = User.builder()
                        .id(Long.valueOf(authUser.getUuid()))
                        .username(authUser.getUsername())
                        .avatar(authUser.getAvatar())
                        .nickname(authUser.getNickname())
                        .password(enPassword)
                        .email(authUser.getEmail())
                        .registerType(type)
                        .registerIp(ipAddr)
                        .loginType(type)
                        .loginIp(ipAddr)
                        .loginTime(new Date())
                        .build();
                if (userService.save(user)) {
                    ipService.refreshIpDetailAsyncByUidAndRegister(user.getId());
                }
            }
            User user = User.builder().id(Long.valueOf(authUser.getUuid())).password(enPassword).build();
            userMapper.updateById(user);
            switch (type) {
                case 1:
                    return OauthConst.LOGIN_TYPE_PARAM
                            + RegisterOrLoginTypeEnum.GITEE.getStrategy()
                            + OauthConst.ACCESS_TOKEN_PARAM
                            + authUser.getToken().getAccessToken()
                            + OauthConst.USER_NAME_PARAM
                            + authUser.getUsername();
                case 2:
                    return OauthConst.LOGIN_TYPE_PARAM
                            + RegisterOrLoginTypeEnum.GITHUB.getStrategy()
                            + OauthConst.ACCESS_TOKEN_PARAM
                            + authUser.getToken().getAccessToken()
                            + OauthConst.USER_NAME_PARAM
                            + authUser.getUsername();
            }
        } else {
            return authResponse.getMsg();
        }
        return null;
    }
}
