package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.Device;
import com.rental.entity.RentalComment;
import com.rental.entity.RentalOrder;
import com.rental.entity.SysUser;
import com.rental.mapper.RentalCommentMapper;
import com.rental.service.DeviceService;
import com.rental.service.RentalCommentService;
import com.rental.service.RentalOrderService;
import com.rental.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RentalCommentServiceImpl extends ServiceImpl<RentalCommentMapper, RentalComment> implements RentalCommentService {

    @Autowired
    private RentalOrderService orderService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private DeviceService deviceService;

    @Override
    public void addComment(RentalComment comment) {
        RentalOrder order = validateOrder(comment.getOrderId(), comment.getUserId(), true);
        RentalComment existingComment = getUserComment(comment.getUserId(), comment.getOrderId());
        if (existingComment != null) {
            throw new RuntimeException("该订单已评价，请使用修改功能");
        }

        SysUser user = getUser(comment.getUserId());
        comment.setUsername(user.getUsername());
        comment.setDeviceId(order.getDeviceId());
        comment.setCreateTime(LocalDateTime.now());

        this.save(comment);
    }

    @Override
    public List<RentalComment> getByDeviceId(Long deviceId) {
        List<RentalComment> comments = this.list(new LambdaQueryWrapper<RentalComment>()
                .eq(RentalComment::getDeviceId, deviceId)
                .orderByDesc(RentalComment::getCreateTime));
        fillDisplayFields(comments);
        return comments;
    }

    @Override
    public void updateComment(RentalComment comment) {
        if (comment.getId() == null) {
            throw new RuntimeException("评论不存在");
        }

        RentalComment existingComment = this.getById(comment.getId());
        if (existingComment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!Objects.equals(existingComment.getUserId(), comment.getUserId())) {
            throw new RuntimeException("无权修改该评论");
        }

        validateOrder(existingComment.getOrderId(), comment.getUserId(), true);
        SysUser user = getUser(comment.getUserId());

        existingComment.setUsername(user.getUsername());
        existingComment.setRating(comment.getRating());
        existingComment.setContent(comment.getContent());
        this.updateById(existingComment);
    }

    @Override
    public void deleteCommentByUser(Long commentId, Long userId) {
        RentalComment existingComment = this.getById(commentId);
        if (existingComment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!Objects.equals(existingComment.getUserId(), userId)) {
            throw new RuntimeException("无权删除该评论");
        }

        this.removeById(commentId);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        RentalComment existingComment = this.getById(commentId);
        if (existingComment == null) {
            throw new RuntimeException("评论不存在");
        }

        this.removeById(commentId);
    }

    @Override
    public List<RentalComment> getByUserId(Long userId) {
        List<RentalComment> comments = this.list(new LambdaQueryWrapper<RentalComment>()
                .eq(RentalComment::getUserId, userId)
                .orderByDesc(RentalComment::getCreateTime));
        fillDisplayFields(comments);
        return comments;
    }

    @Override
    public RentalComment getUserComment(Long userId, Long orderId) {
        RentalComment comment = this.getOne(new LambdaQueryWrapper<RentalComment>()
                .eq(RentalComment::getUserId, userId)
                .eq(RentalComment::getOrderId, orderId)
                .last("limit 1"));
        if (comment != null) {
            fillDisplayFields(List.of(comment));
        }
        return comment;
    }

    @Override
    public void fillDisplayFields(List<RentalComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }

        Set<Long> orderIds = comments.stream()
                .map(RentalComment::getOrderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> deviceIds = comments.stream()
                .map(RentalComment::getDeviceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, RentalOrder> orderMap = orderIds.isEmpty()
                ? Map.of()
                : orderService.listByIds(orderIds).stream()
                .collect(Collectors.toMap(RentalOrder::getId, item -> item));
        Map<Long, Device> deviceMap = deviceIds.isEmpty()
                ? Map.of()
                : deviceService.listByIds(deviceIds).stream()
                .collect(Collectors.toMap(Device::getId, item -> item));

        for (RentalComment comment : comments) {
            RentalOrder order = orderMap.get(comment.getOrderId());
            if (order != null) {
                comment.setOrderNo(order.getOrderNo());
            }

            Device device = deviceMap.get(comment.getDeviceId());
            if (device != null) {
                comment.setDeviceName(device.getName());
            }
        }
    }

    private RentalOrder validateOrder(Long orderId, Long userId, boolean requireFinished) {
        RentalOrder order = orderService.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new RuntimeException("无权操作该订单评论");
        }
        if (requireFinished && order.getStatus() != 4) {
            throw new RuntimeException("订单未完成，无法评价");
        }
        return order;
    }

    private SysUser getUser(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
}
