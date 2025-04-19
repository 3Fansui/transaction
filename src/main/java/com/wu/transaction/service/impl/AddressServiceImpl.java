package com.wu.transaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.transaction.entity.po.Address;
import com.wu.transaction.mapper.AddressMapper;
import com.wu.transaction.service.IAddressService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户地址信息表 服务实现类
 * </p>
 *
 * @author Fs
 * @since 2025-04-11
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {
    
} 