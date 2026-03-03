package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.UserAuth;

public interface UserAuthService extends IService<UserAuth> {
    UserAuth getByUserId(Long userId);
    void applyAuth(UserAuth auth);
    void auditAuth(Long authId, Integer status, String remark);
}
