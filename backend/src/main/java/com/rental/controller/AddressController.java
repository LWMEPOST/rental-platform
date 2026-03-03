package com.rental.controller;

import com.rental.common.result.Result;
import com.rental.entity.UserAddress;
import com.rental.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private UserAddressService addressService;

    @GetMapping("/list")
    public Result<List<UserAddress>> list(@RequestParam Long userId) {
        return Result.success(addressService.getListByUserId(userId));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody UserAddress address) {
        if (addressService.save(address)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody UserAddress address) {
        if (addressService.updateById(address)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (addressService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @PostMapping("/setDefault")
    public Result<String> setDefault(@RequestBody UserAddress address) {
        addressService.setDefault(address.getUserId(), address.getId());
        return Result.success("设置成功");
    }
}
