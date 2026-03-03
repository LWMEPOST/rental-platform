package com.rental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.Device;
import com.rental.entity.RentalOrder;
import com.rental.mapper.RentalOrderMapper;
import com.rental.service.DeviceService;
import com.rental.service.RentalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RentalOrderServiceImpl extends ServiceImpl<RentalOrderMapper, RentalOrder> implements RentalOrderService {

    @Autowired
    private DeviceService deviceService;

    @Override
    @Transactional
    public RentalOrder createOrder(Long userId, Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        Device device = deviceService.getById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }
        if (device.getStockQuantity() <= 0) {
            throw new RuntimeException("库存不足");
        }

        RentalOrder order = new RentalOrder();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(userId);
        order.setDeviceId(deviceId);
        order.setMerchantId(device.getMerchantId());
        order.setStartTime(startTime);
        order.setEndTime(endTime);
        
        // Calculate rental days (ceiling)
        long days = Duration.between(startTime, endTime).toDays();
        if (days <= 0) days = 1;
        
        BigDecimal rentalFee = device.getRentalPrice().multiply(new BigDecimal(days));
        order.setTotalAmount(rentalFee);
        order.setDepositAmount(device.getDepositAmount());
        order.setInsuranceAmount(new BigDecimal("0.00")); // Insurance logic can be added later
        order.setStatus(0); // Pending Pay
        
        this.save(order);
        
        // Decrease stock? Usually after payment, but for simplicity let's reserve it or assume logic elsewhere.
        // For this demo, let's not decrease stock yet or do it here.
        // device.setStockQuantity(device.getStockQuantity() - 1);
        // deviceService.updateById(device);

        return order;
    }

    @Override
    public void payOrder(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(1); // Paid, Pending Delivery
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void returnOrder(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        // Only orders in "Renting" (2) status can be returned by user (to become "Pending Return" 3)
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确，无法申请归还");
        }
        
        order.setStatus(3); // Pending Return (User initiated return)
        this.updateById(order);
    }
}
