package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.RentalOrder;

import java.time.LocalDateTime;

public interface RentalOrderService extends IService<RentalOrder> {
    RentalOrder createOrder(Long userId, Long deviceId, LocalDateTime startTime, LocalDateTime endTime);
    void payOrder(Long orderId);
    void returnOrder(Long orderId);
}
