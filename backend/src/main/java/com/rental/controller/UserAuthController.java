package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.result.Result;
import com.rental.entity.UserAuth;
import com.rental.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/identity")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/status")
    public Result<UserAuth> getStatus(@RequestParam Long userId) {
        return Result.success(userAuthService.getByUserId(userId));
    }

    @PostMapping("/apply")
    public Result<String> apply(@RequestBody UserAuth auth) {
        userAuthService.applyAuth(auth);
        return Result.success("提交成功，请等待审核");
    }

    // Admin: List all auth requests
    @GetMapping("/admin/list")
    public Result<Page<UserAuth>> list(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(required = false) Integer status) {
        Page<UserAuth> authPage = new Page<>(page, size);
        LambdaQueryWrapper<UserAuth> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(UserAuth::getStatus, status);
        }
        wrapper.orderByDesc(UserAuth::getCreateTime);
        return Result.success(userAuthService.page(authPage, wrapper));
    }

    // Admin: Audit
    @PostMapping("/audit")
    public Result<String> audit(@RequestBody AuditRequest request) {
        userAuthService.auditAuth(request.getAuthId(), request.getStatus(), request.getRemark());
        return Result.success("审核完成");
    }

    public static class AuditRequest {
        private Long authId;
        private Integer status;
        private String remark;
        // getters setters
        public Long getAuthId() { return authId; }
        public void setAuthId(Long authId) { this.authId = authId; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}
