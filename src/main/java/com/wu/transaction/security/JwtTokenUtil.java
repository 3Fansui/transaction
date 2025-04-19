package com.wu.transaction.security;

import com.wu.transaction.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtTokenUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // 生成Token
    public String generateToken(UserDetails userDetails) {
        long now = System.currentTimeMillis();
        
        // 收集用户权限
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        // 创建令牌，使用HS256算法
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // 从令牌中获取用户名
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    // 从令牌中获取用户权限
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("authorities").toString().split(","))
                        .filter(auth -> !auth.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        
        User principal = new User(claims.getSubject(), "", authorities);
        
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    
    // 验证令牌是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 