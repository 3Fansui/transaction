package com.wu.transaction.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wu.transaction.entity.po.User;
import com.wu.transaction.security.JwtTokenUtil;
import com.wu.transaction.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户控制器", description = "用户注册、登录、信息管理等功能")
public class UserController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账号")
    public ResponseEntity<?> register(@RequestBody User user) {
        // 检查用户名是否已存在
        User existingUser = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        
        if (existingUser != null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 设置初始余额
        user.setBalance(BigDecimal.ZERO);
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean success = userService.save(user);
        
        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("userId", user.getUserId());
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "注册失败，请稍后重试");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户名和密码并返回JWT令牌")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        // 使用Spring Security认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        
        // 设置认证信息到上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 获取用户详情
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // 生成JWT令牌
        String token = jwtTokenUtil.generateToken(userDetails);
        
        // 查询用户信息(排除密码)
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .select(User.class, info -> !info.getColumn().equals("password")));
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "验证旧密码并更新为新密码")
    public boolean changePassword(@RequestParam Integer userId,
                                @RequestParam String oldPassword,
                                @RequestParam String newPassword) {
        User user = userService.getById(userId);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            return userService.updateById(user);
        }
        return false;
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    public boolean updateUserInfo(@RequestBody User user) {
        // 不允许更新密码，如果传入了密码字段则置空
        user.setPassword(null);
        return userService.updateById(user);
    }

    @GetMapping("/info/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取详细信息")
    public User getUserInfo(@PathVariable Integer userId) {
        return userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, userId)
                .select(User.class, info -> !info.getColumn().equals("password")));
    }

    @PostMapping("/recharge")
    @Operation(summary = "余额充值", description = "为指定用户增加账户余额")
    public boolean recharge(@RequestParam Integer userId, @RequestParam BigDecimal amount) {
        User user = userService.getById(userId);
        if (user != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            user.setBalance(user.getBalance().add(amount));
            return userService.updateById(user);
        }
        return false;
    }
} 