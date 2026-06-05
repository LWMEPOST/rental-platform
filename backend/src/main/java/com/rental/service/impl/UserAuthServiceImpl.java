package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.UserAuth;
import com.rental.mapper.UserAuthMapper;
import com.rental.service.UserAuthService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {

    @Override
    public UserAuth getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<UserAuth>().eq(UserAuth::getUserId, userId));
    }

    @Override
    public void applyAuth(UserAuth auth) {
        if (auth == null || auth.getUserId() == null) {
            throw new RuntimeException("用户信息不能为空");
        }
        if (!StringUtils.hasText(auth.getRealName())) {
            throw new RuntimeException("真实姓名不能为空");
        }

        String realName = auth.getRealName().trim();
        if (realName.length() < 2 || realName.length() > 20) {
            throw new RuntimeException("真实姓名长度需在2-20个字符之间");
        }
        auth.setRealName(realName);

        UserAuth existing = getByUserId(auth.getUserId());
        if (existing != null) {
            // Update existing application
            auth.setId(existing.getId());
            auth.setStatus(0); // Reset to pending
            auth.setAuditRemark(null);
            auth.setAuditTime(null);
            this.updateById(auth);
        } else {
            // New application
            auth.setStatus(0);
            this.save(auth);
        }
    }

    @Override
    public void auditAuth(Long authId, Integer status, String remark) {
        UserAuth auth = this.getById(authId);
        if (auth == null) {
            throw new RuntimeException("认证申请不存在");
        }
        auth.setStatus(status);
        auth.setAuditRemark(remark);
        auth.setAuditTime(LocalDateTime.now());
        this.updateById(auth);
    }
}
