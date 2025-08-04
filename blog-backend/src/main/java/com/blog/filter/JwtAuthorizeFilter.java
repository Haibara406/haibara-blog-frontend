package com.blog.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.blog.constants.RespConst;
import com.blog.domain.entity.LoginUser;
import com.blog.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author haibara
 * @description jwt认证过滤器
 * @since 2025/7/27 17:37
 */

@Component
@RequiredArgsConstructor
public class JwtAuthorizeFilter extends OncePerRequestFilter {


    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(RespConst.TOKEN_HEADER);
        // 解析jwt
        DecodedJWT jwt = jwtUtils.resolveJwt(header);

        if(!ObjectUtils.isEmpty(jwt)){
            // 获取UserDetails
            LoginUser user = (LoginUser) jwtUtils.toUser(jwt);
            // 创建认证对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            // 保存认证详细信息
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 验证通过，设置上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
