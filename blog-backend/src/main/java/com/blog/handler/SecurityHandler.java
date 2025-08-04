package com.blog.handler;

import com.blog.constants.Const;
import com.blog.constants.RespConst;
import com.blog.domain.entity.LoginUser;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.AuthorizeVO;
import com.blog.enums.RegisterOrLoginTypeEnum;
import com.blog.enums.RespEnum;
import com.blog.enums.StateEnum;
import com.blog.service.LoginLogService;
import com.blog.service.UserService;
import com.blog.utils.JwtUtils;
import com.blog.utils.RedisCache;
import com.blog.utils.StringUtils;
import com.blog.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author haibara
 * @description SpringSecurity处理器
 * @since 2025/7/27 17:34
 */

@Component
public class SecurityHandler {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisCache redisCache;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private UserService userService;

    public static final String USER_NAME = "username";


    /**
     * 登录成功处理
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 认证信息
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        handlerOnAuthenticationSuccess(request,response,(LoginUser)authentication.getPrincipal());
    }

    /**
     * 处理认证成功逻辑
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param user 登录用户信息
     */
    public void handlerOnAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, LoginUser user) {
        String typeHeader = request.getHeader(Const.TYPE_HEADER);
        if ((!StringUtils.matches(typeHeader, List.of(Const.BACKEND_REQUEST, Const.FRONTEND_REQUEST)) && Objects.equals(user.getUser().getRegisterType(), RegisterOrLoginTypeEnum.EMAIL.getRegisterType()))) {
            throw new BadCredentialsException("非法请求");
        }
        Long id = user.getUser().getId();
        String name = user.getUser().getUsername();
        // 生成jwt
        String uuid = UUID.randomUUID().toString();
        String token = jwtUtils.createJwt(uuid, user, id, name);

        // 转换VO
        AuthorizeVO authorizeVO = user.getUser().asViewObject(AuthorizeVO.class, v -> {
            v.setToken(token);
            v.setExpire(jwtUtils.expireTime());
        });
        // 更新用户登录状态
        userService.userLoginStatus(user.getUser().getId(), user.getUser().getRegisterType());
        loginLogService.loginLog(request, request.getParameter(USER_NAME), StateEnum.STATE_NORMAL.getStatus(), RespConst.SUCCESS_LOGIN_MSG);
        WebUtil.renderString(response, ResponseResult.success(authorizeVO, RespConst.SUCCESS_LOGIN_MSG).asJsonString());
    }


    /**
     * 登录失败处理
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param exception 认证异常
     * @throws IOException IO异常
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        loginLogService.loginLog(request, request.getParameter(USER_NAME), StateEnum.STATE_FAILED.getStatus(), exception.getMessage());
        WebUtil.renderString(response, ResponseResult.failure(RespEnum.USERNAME_OR_PASSWORD_ERROR.getCode(), exception.getMessage()).asJsonString());
    }

    /**
     * 退出登录处理
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param authentication 认证信息
     */
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        boolean invalidateJwt = jwtUtils.invalidateJwt(request.getHeader("Authorization"));
        if (invalidateJwt) {
            WebUtil.renderString(response, ResponseResult.success().asJsonString());
            return;
        }
        WebUtil.renderString(response, ResponseResult.failure(RespEnum.NOT_LOGIN.getCode(), RespEnum.NOT_LOGIN.getMsg()).asJsonString());
    }

    /**
     * 没有登录处理
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param exception 认证异常
     * @throws IOException IO异常
     */
    public void onUnAuthenticated(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        WebUtil.renderString(response, ResponseResult.failure(RespEnum.NOT_LOGIN.getCode(), RespEnum.NOT_LOGIN.getMsg()).asJsonString());
    }

    /**
     * 没有权限处理
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param exception 访问拒绝异常
     */
    public void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {

        WebUtil.renderString(response, ResponseResult.failure(RespEnum.NO_PERMISSION.getCode(), RespEnum.NO_PERMISSION.getMsg()).asJsonString());
    }
}
