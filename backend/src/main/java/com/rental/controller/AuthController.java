package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.result.Result;
import com.rental.entity.SysUser;
import com.rental.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody SysUser loginUser) {
        try {
            SysUser user = sysUserService.loginByPhone(loginUser.getPhone(), loginUser.getPassword());
            if (user != null) {
                return Result.success(user);
            }
            return Result.error("手机号或密码错误");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser user) {
        try {
            sysUserService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // Admin: List Users
    @GetMapping("/admin/user/list")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<SysUser>> userList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String keyword) {
        Page<SysUser> userPage = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                              .or()
                              .like(SysUser::getPhone, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return Result.success(sysUserService.getUserListWithExtraStatus(userPage, wrapper));
    }
    
    // Admin: Toggle User Status (Freeze/Unfreeze)
    @PutMapping("/admin/user/status")
    public Result<String> toggleUserStatus(@RequestBody SysUser user) {
        SysUser dbUser = sysUserService.getById(user.getId());
        if (dbUser == null) return Result.error("用户不存在");
        
        // Only allow toggling status between 0 and 1
        dbUser.setStatus(user.getStatus());
        sysUserService.updateById(dbUser);
        return Result.success("状态更新成功");
    }
}
