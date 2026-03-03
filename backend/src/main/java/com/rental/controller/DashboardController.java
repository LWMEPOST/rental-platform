package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rental.common.result.Result;
import com.rental.entity.Device;
import com.rental.entity.RentalOrder;
import com.rental.entity.SysUser;
import com.rental.service.DeviceService;
import com.rental.service.RentalOrderService;
import com.rental.service.SysUserService;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.rental.entity.DeviceCategory;
import com.rental.service.DeviceCategoryService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private RentalOrderService orderService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private DeviceCategoryService categoryService;

    // ... existing stats method ...

    @GetMapping("/analysis")
    public Result<AnalysisData> getAnalysis() {
        // ... (Trend logic same as before) ...
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<RentalOrder> recentOrders = orderService.list(new LambdaQueryWrapper<RentalOrder>()
                .ge(RentalOrder::getCreateTime, sevenDaysAgo));
        
        Map<String, Long> trendMap = recentOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCreateTime().format(DateTimeFormatter.ofPattern("MM-dd")),
                        Collectors.counting()
                ));
        
        List<String> dates = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern("MM-dd"));
            dates.add(date);
            orderCounts.add(trendMap.getOrDefault(date, 0L));
        }

        // 2. Category Distribution
        List<Map<String, Object>> categoryData = categoryService.getCategoryDeviceCounts();
        
        // 3. Top Devices (Hot Models)
        List<RentalOrder> allOrders = orderService.list();
        Map<Long, Long> deviceOrderCount = allOrders.stream()
                .collect(Collectors.groupingBy(RentalOrder::getDeviceId, Collectors.counting()));
        
        List<Long> topDeviceIds = deviceOrderCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        List<String> topDeviceNames = new ArrayList<>();
        List<Long> topDeviceCounts = new ArrayList<>();
        
        if (!topDeviceIds.isEmpty()) {
            List<Device> topDevices = deviceService.listByIds(topDeviceIds);
            Map<Long, String> deviceNameMap = topDevices.stream()
                    .collect(Collectors.toMap(Device::getId, Device::getName));
            
            for (Long id : topDeviceIds) {
                topDeviceNames.add(deviceNameMap.getOrDefault(id, "Unknown Device"));
                topDeviceCounts.add(deviceOrderCount.get(id));
            }
        }

        return Result.success(AnalysisData.builder()
                .trendDates(dates)
                .trendValues(orderCounts)
                .categoryData(categoryData)
                .topDeviceNames(topDeviceNames)
                .topDeviceCounts(topDeviceCounts)
                .build());
    }
    
    @Data
    @Builder
    public static class AnalysisData {
        private List<String> trendDates;
        private List<Long> trendValues;
        private List<Map<String, Object>> categoryData;
        private List<String> topDeviceNames;
        private List<Long> topDeviceCounts;
    }

    // ... existing classes ...

    @GetMapping("/stats")
    public Result<DashboardStats> getStats() {
        // 1. Total Orders
        long totalOrders = orderService.count();
        
        // 2. Total Revenue (Sum of paid/finished orders)
        // Status: 1-Pending Delivery, 2-Renting, 3-Pending Return, 4-Finished
        // We count totalAmount for these statuses.
        // For simplicity, let's sum totalAmount of all non-cancelled, non-pending-pay orders
        // Or strictly paid ones.
        List<RentalOrder> paidOrders = orderService.list(new LambdaQueryWrapper<RentalOrder>()
                .in(RentalOrder::getStatus, 1, 2, 3, 4));
        BigDecimal totalRevenue = paidOrders.stream()
                .map(RentalOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Rented Devices (Active Rentals)
        long rentingCount = orderService.count(new LambdaQueryWrapper<RentalOrder>()
                .eq(RentalOrder::getStatus, 2)); // 2-Renting

        // 4. Registered Users
        long userCount = userService.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "CLIENT"));

        // 5. Pending Todos (Mock logic for now, or real queries)
        List<TodoItem> todos = new ArrayList<>();
        
        // Check for low stock devices (< 3)
        long lowStockCount = deviceService.count(new LambdaQueryWrapper<Device>()
                .lt(Device::getStockQuantity, 3)
                .eq(Device::getStatus, 1));
        if (lowStockCount > 0) {
            todos.add(new TodoItem(lowStockCount + " 款设备库存不足", "现在", "待补货"));
        }

        // Check for Pending Return orders
        long pendingReturnCount = orderService.count(new LambdaQueryWrapper<RentalOrder>()
                .eq(RentalOrder::getStatus, 3));
        if (pendingReturnCount > 0) {
            todos.add(new TodoItem(pendingReturnCount + " 个订单待确认归还", "现在", "待处理"));
        }
        
        // Check for Pending Delivery orders
        long pendingDeliveryCount = orderService.count(new LambdaQueryWrapper<RentalOrder>()
                .eq(RentalOrder::getStatus, 1));
        if (pendingDeliveryCount > 0) {
             todos.add(new TodoItem(pendingDeliveryCount + " 个订单待发货", "现在", "待发货"));
        }

        return Result.success(DashboardStats.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .rentingDevices(rentingCount)
                .userCount(userCount)
                .todos(todos)
                .build());
    }

    @Data
    @Builder
    public static class DashboardStats {
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private Long rentingDevices;
        private Long userCount;
        private List<TodoItem> todos;
    }

    @Data
    public static class TodoItem {
        private String title;
        private String time;
        private String status;

        public TodoItem(String title, String time, String status) {
            this.title = title;
            this.time = time;
            this.status = status;
        }
    }
}
