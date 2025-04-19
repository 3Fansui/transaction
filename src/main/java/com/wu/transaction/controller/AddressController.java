package com.wu.transaction.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wu.transaction.entity.po.Address;
import com.wu.transaction.service.IAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@Tag(name = "地址控制器", description = "收货地址管理相关功能")
public class AddressController {

    private final IAddressService addressService;

    @PostMapping("/add")
    @Operation(summary = "添加地址", description = "添加新的收货地址")
    public boolean addAddress(@RequestBody Address address) {
        return addressService.save(address);
    }

    @PutMapping("/update")
    @Operation(summary = "更新地址", description = "更新已有的收货地址信息")
    public boolean updateAddress(@RequestBody Address address) {
        return addressService.updateById(address);
    }

    @DeleteMapping("/delete/{addressId}")
    @Operation(summary = "删除地址", description = "删除指定ID的收货地址")
    public boolean deleteAddress(@PathVariable Integer addressId) {
        return addressService.removeById(addressId);
    }

    @GetMapping("/list/{userId}")
    @Operation(summary = "获取用户地址列表", description = "获取指定用户的所有收货地址")
    public List<Address> getUserAddresses(@PathVariable Integer userId) {
        return addressService.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault));
    }

    @PostMapping("/set-default/{userId}/{addressId}")
    @Operation(summary = "设置默认地址", description = "将指定地址设为用户的默认收货地址")
    public boolean setDefaultAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        // 先将用户所有地址设置为非默认
        addressService.update(
            new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, false)
        );

        // 设置新的默认地址
        return addressService.update(
            new LambdaUpdateWrapper<Address>()
                .eq(Address::getAddressId, addressId)
                .set(Address::getIsDefault, true)
        );
    }
} 