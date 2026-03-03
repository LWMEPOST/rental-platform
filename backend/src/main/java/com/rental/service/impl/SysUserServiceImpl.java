package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.RentalOrder;
import com.rental.entity.SysUser;
import com.rental.entity.UserAuth;
import com.rental.mapper.RentalOrderMapper;
import com.rental.mapper.SysUserMapper;
import com.rental.mapper.UserAuthMapper;
import com.rental.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private UserAuthMapper userAuthMapper;
    
    @Autowired
    private RentalOrderMapper rentalOrderMapper;

    @Override
    public SysUser login(String username, String password) {
        // 1. Try finding user by username
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = this.getOne(wrapper);

        if (user == null) {
            return null;
        }

        // 2. Check password
        String inputMd5 = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        
        if (user.getPassword().equals(inputMd5)) {
            return user;
        }
        
        return null;
    }

    @Override
    public void register(SysUser user) {
        // Validate input
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new RuntimeException("用户名或密码不能为空");
        }

        // Check if username exists
        LambdaQueryWrapper<SysUser> queryUsername = new LambdaQueryWrapper<>();
        queryUsername.eq(SysUser::getUsername, user.getUsername());
        if (this.count(queryUsername) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // Check if phone exists (if phone is provided)
        if (StringUtils.hasText(user.getPhone())) {
            LambdaQueryWrapper<SysUser> queryPhone = new LambdaQueryWrapper<>();
            queryPhone.eq(SysUser::getPhone, user.getPhone());
            if (this.count(queryPhone) > 0) {
                throw new RuntimeException("手机号已被注册");
            }
        }

        // Encrypt password
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(md5Password);
        
        // Set default values if needed
        if (user.getRole() == null) {
            user.setRole("CLIENT");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        this.save(user);
    }
    
    @Override
    public IPage<SysUser> getUserListWithExtraStatus(Page<SysUser> page, LambdaQueryWrapper<SysUser> wrapper) {
        IPage<SysUser> result = this.page(page, wrapper);
        List<SysUser> users = result.getRecords();
        if (users.isEmpty()) return result;
        
        List<Long> userIds = users.stream().map(SysUser::getId).collect(Collectors.toList());
        
        // Batch fetch auth status
        LambdaQueryWrapper<UserAuth> authWrapper = new LambdaQueryWrapper<>();
        authWrapper.in(UserAuth::getUserId, userIds);
        List<UserAuth> auths = userAuthMapper.selectList(authWrapper);
        Map<Long, Integer> authMap = auths.stream()
                .collect(Collectors.toMap(UserAuth::getUserId, UserAuth::getStatus));
                
        // Batch fetch pending return count (Status 2=Renting, 3=Pending Return)
        // We consider these as "Unreturned"
        LambdaQueryWrapper<RentalOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(RentalOrder::getUserId, userIds)
                    .in(RentalOrder::getStatus, 2, 3); 
        List<RentalOrder> orders = rentalOrderMapper.selectList(orderWrapper);
        Map<Long, Long> returnMap = orders.stream()
                .collect(Collectors.groupingBy(RentalOrder::getUserId, Collectors.counting()));
                
        for (SysUser user : users) {
            // Default auth status null (Not Applied) or value from DB
            // Let's define: -1 Not Applied, 0 Pending, 1 Verified, 2 Rejected
            user.setAuthStatus(authMap.getOrDefault(user.getId(), -1));
            user.setPendingReturnCount(returnMap.getOrDefault(user.getId(), 0L));
        }
        
        return result;
    }
}
