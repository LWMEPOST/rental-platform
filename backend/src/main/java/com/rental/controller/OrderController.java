package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.result.Result;
import com.rental.entity.RentalOrder;
import com.rental.service.RentalOrderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private RentalOrderService rentalOrderService;

    @PostMapping("/create")
    public Result<RentalOrder> createOrder(@RequestBody CreateOrderRequest request) {
        RentalOrder order = rentalOrderService.createOrder(
                request.getUserId(),
                request.getDeviceId(),
                request.getStartTime(),
                request.getEndTime()
        );
        return Result.success(order);
    }

    @PostMapping("/pay/{id}")
    public Result<String> payOrder(@PathVariable Long id) {
        rentalOrderService.payOrder(id);
        return Result.success("支付成功");
    }

    @PostMapping("/return/{id}")
    public Result<String> returnOrder(@PathVariable Long id) {
        rentalOrderService.returnOrder(id);
        return Result.success("归还申请提交成功，请等待管理员确认");
    }

    @GetMapping("/detail/{id}")
    public Result<RentalOrder> getDetail(@PathVariable Long id) {
        RentalOrder order = rentalOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<Page<RentalOrder>> list(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam Long userId) {
        Page<RentalOrder> orderPage = new Page<>(page, size);
        LambdaQueryWrapper<RentalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RentalOrder::getUserId, userId);
        wrapper.orderByDesc(RentalOrder::getCreateTime);
        return Result.success(rentalOrderService.page(orderPage, wrapper));
    }

    // Admin: List all orders
    @GetMapping("/admin/list")
    public Result<Page<RentalOrder>> adminList(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String orderNo,
                                               @RequestParam(required = false) Integer status) {
        Page<RentalOrder> orderPage = new Page<>(page, size);
        LambdaQueryWrapper<RentalOrder> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(RentalOrder::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(RentalOrder::getStatus, status);
        }
        wrapper.orderByDesc(RentalOrder::getCreateTime);
        return Result.success(rentalOrderService.page(orderPage, wrapper));
    }

    // Admin: Update Order Status (e.g., Deliver, Return, Cancel)
    @PutMapping("/admin/updateStatus")
    public Result<String> updateStatus(@RequestBody UpdateStatusRequest request) {
        RentalOrder order = rentalOrderService.getById(request.getOrderId());
        if (order == null) return Result.error("订单不存在");
        
        order.setStatus(request.getStatus());
        if (request.getStatus() == 2) { // Renting / Delivered
             order.setDeliveryTime(LocalDateTime.now());
        } else if (request.getStatus() == 4) { // Finished / Returned
             order.setReturnTime(LocalDateTime.now());
        }
        
        rentalOrderService.updateById(order);
        return Result.success("状态更新成功");
    }

    @Data
    public static class CreateOrderRequest {
        private Long userId;
        private Long deviceId;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
    }
    
    @Data
    public static class UpdateStatusRequest {
        private Long orderId;
        private Integer status;
    }
}
