package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.RentalComment;
import com.rental.entity.RentalOrder;
import com.rental.entity.SysUser;
import com.rental.mapper.RentalCommentMapper;
import com.rental.service.RentalCommentService;
import com.rental.service.RentalOrderService;
import com.rental.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalCommentServiceImpl extends ServiceImpl<RentalCommentMapper, RentalComment> implements RentalCommentService {

    @Autowired
    private RentalOrderService orderService;
    @Autowired
    private SysUserService userService;

    @Override
    public void addComment(RentalComment comment) {
        // 1. Check if order exists and is finished
        RentalOrder order = orderService.getById(comment.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 4) { // Must be Finished
            throw new RuntimeException("订单未完成，无法评价");
        }
        
        // 2. Fill info
        SysUser user = userService.getById(comment.getUserId());
        comment.setUsername(user.getUsername()); // or nickname
        comment.setDeviceId(order.getDeviceId());
        comment.setCreateTime(LocalDateTime.now());
        
        this.save(comment);
    }

    @Override
    public List<RentalComment> getByDeviceId(Long deviceId) {
        return this.list(new LambdaQueryWrapper<RentalComment>()
                .eq(RentalComment::getDeviceId, deviceId)
                .orderByDesc(RentalComment::getCreateTime));
    }
}
