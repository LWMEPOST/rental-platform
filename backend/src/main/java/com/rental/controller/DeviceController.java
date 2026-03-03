package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.result.Result;
import com.rental.entity.Device;
import com.rental.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    // List devices with pagination and search
    @GetMapping("/list")
    public Result<Page<Device>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) Integer status) {
        Page<Device> devicePage = new Page<>(page, size);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        
        // Search by name or description
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Device::getName, keyword)
                              .or()
                              .like(Device::getDescription, keyword));
        }
        
        // Filter by category using SubQuery or Exists
        if (categoryId != null) {
            // WHERE id IN (SELECT device_id FROM device_category_mapping WHERE category_id = ?)
            wrapper.inSql(Device::getId, "SELECT device_id FROM device_category_mapping WHERE category_id = " + categoryId);
        }
        
        if (status != null) {
            wrapper.eq(Device::getStatus, status);
        }
        
        // Order by create time desc
        wrapper.orderByDesc(Device::getCreateTime);

        Page<Device> result = deviceService.page(devicePage, wrapper);
        
        // Fill category info
        deviceService.fillCategories(result.getRecords());
        
        return Result.success(result);
    }
    
    // Separate list for Admin (shows all)
    @GetMapping("/admin/list")
    public Result<Page<Device>> adminList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Long categoryId) {
        Page<Device> devicePage = new Page<>(page, size);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Device::getName, keyword).or().like(Device::getDescription, keyword));
        }
        if (categoryId != null) {
             wrapper.inSql(Device::getId, "SELECT device_id FROM device_category_mapping WHERE category_id = " + categoryId);
        }
        wrapper.orderByDesc(Device::getCreateTime);
        
        Page<Device> result = deviceService.page(devicePage, wrapper);
        deviceService.fillCategories(result.getRecords());
        
        return Result.success(result);
    }


    // Get device detail
    @GetMapping("/{id}")
    public Result<Device> getDetail(@PathVariable Long id) {
        Device device = deviceService.getById(id);
        if (device == null) {
            return Result.error("Device not found");
        }
        return Result.success(device);
    }

    // Add Device
    @PostMapping("/add")
    public Result<String> add(@RequestBody Device device) {
        if (deviceService.save(device)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    // Update Device
    @PutMapping("/update")
    public Result<String> update(@RequestBody Device device) {
        if (deviceService.updateById(device)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    // Delete Device
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (deviceService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
