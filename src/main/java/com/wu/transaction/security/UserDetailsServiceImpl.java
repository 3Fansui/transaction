package com.wu.transaction.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wu.transaction.entity.po.User;
import com.wu.transaction.service.IUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserService userService;

    public UserDetailsServiceImpl(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        
        // 如果用户不存在，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在: " + username);
        }
        
        // 返回Spring Security的UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 