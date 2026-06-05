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
import java.time.format.DateTimeFormatter;
import java.util.Random;

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
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        order.setOrderNo("ORD" + timeStr + randomStr);
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
    @Transactional
    public void payOrder(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }

        changeDeviceStock(order.getDeviceId(), -1);
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

    @Override
    public void cancelOrder(Long orderId) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("当前订单状态不可取消");
        }

        order.setStatus(5); // Cancelled
        this.updateById(order);
    }

    @Override
    @Transactional
    public void updateStatusByAdmin(Long orderId, Integer status) {
        RentalOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (status == null) {
            throw new RuntimeException("目标状态不能为空");
        }

        if (status == 2) {
            if (order.getStatus() != 1) {
                throw new RuntimeException("只有待发货订单才能发货");
            }
            order.setStatus(2);
            order.setDeliveryTime(LocalDateTime.now());
            this.updateById(order);
            return;
        }

        if (status == 4) {
            if (order.getStatus() != 2 && order.getStatus() != 3) {
                throw new RuntimeException("只有租赁中或待归还订单才能确认归还");
            }
            changeDeviceStock(order.getDeviceId(), 1);
            order.setStatus(4);
            order.setReturnTime(LocalDateTime.now());
            this.updateById(order);
            return;
        }

        if (status == 5) {
            if (order.getStatus() == 0) {
                order.setStatus(5);
                this.updateById(order);
                return;
            }
            if (order.getStatus() == 1) {
                changeDeviceStock(order.getDeviceId(), 1);
                order.setStatus(5);
                this.updateById(order);
                return;
            }
            throw new RuntimeException("当前订单状态不可取消");
        }

        throw new RuntimeException("不支持的状态更新");
    }

    private void changeDeviceStock(Long deviceId, int delta) {
        Device device = deviceService.getById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        int currentStock = device.getStockQuantity() == null ? 0 : device.getStockQuantity();
        int nextStock = currentStock + delta;
        if (nextStock < 0) {
            throw new RuntimeException("库存不足");
        }

        device.setStockQuantity(nextStock);
        deviceService.updateById(device);
    }
}
