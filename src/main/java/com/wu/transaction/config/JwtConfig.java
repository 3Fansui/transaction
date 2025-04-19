package com.wu.transaction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private String secret = "ZCprPyacfQyXXU7NiJSJlUebCGYVXeWmJkNhDsYsQXahBWVtkBuQcMHpMeOhyA6"; // 默认秘钥
    private long expiration = 86400000; // 默认过期时间24小时
    private String header = "Authorization"; // 默认请求头名称
    private String tokenPrefix = "Bearer "; // Token前缀
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
    
    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public String getTokenPrefix() {
        return tokenPrefix;
    }
    
    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
} 