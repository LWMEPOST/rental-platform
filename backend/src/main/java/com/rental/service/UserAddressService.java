package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {
    List<UserAddress> getListByUserId(Long userId);
    void setDefault(Long userId, Long addressId);
}
