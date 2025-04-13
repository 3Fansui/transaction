package com.wu.transaction.service.impl;

import com.wu.transaction.entity.po.User;
import com.wu.transaction.mapper.UserMapper;
import com.wu.transaction.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
