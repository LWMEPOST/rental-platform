package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.result.Result;
import com.rental.entity.DeviceCategory;
import com.rental.service.DeviceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private DeviceCategoryService categoryService;

    // List all categories (Tree structure usually handled by frontend or separate DTO, here flat list for simplicity)
    @GetMapping("/list")
    public Result<List<DeviceCategory>> list() {
        LambdaQueryWrapper<DeviceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DeviceCategory::getSortOrder);
        return Result.success(categoryService.list(wrapper));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody DeviceCategory category) {
        if (categoryService.save(category)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody DeviceCategory category) {
        if (categoryService.updateById(category)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (categoryService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
