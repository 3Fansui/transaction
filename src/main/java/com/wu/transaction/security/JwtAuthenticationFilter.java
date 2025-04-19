package com.wu.transaction.security;

import com.wu.transaction.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, JwtConfig jwtConfig) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取Authorization头
        String header = request.getHeader(jwtConfig.getHeader());
        
        // 检查是否包含Token
        if (header == null || !header.startsWith(jwtConfig.getTokenPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 提取Token
        String token = header.replace(jwtConfig.getTokenPrefix(), "");
        
        try {
            // 校验Token有效性
            if (jwtTokenUtil.validateToken(token)) {
                // 获取用户认证信息
                Authentication auth = jwtTokenUtil.getAuthentication(token);
                // 设置到Spring Security上下文
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // 清空认证信息
            SecurityContextHolder.clearContext();
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
} 