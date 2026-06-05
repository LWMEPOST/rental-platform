package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.RentalComment;

import java.util.List;

public interface RentalCommentService extends IService<RentalComment> {
    void addComment(RentalComment comment);
    void updateComment(RentalComment comment);
    void deleteCommentByUser(Long commentId, Long userId);
    void deleteCommentByAdmin(Long commentId);
    List<RentalComment> getByDeviceId(Long deviceId);
    List<RentalComment> getByUserId(Long userId);
    RentalComment getUserComment(Long userId, Long orderId);
    void fillDisplayFields(List<RentalComment> comments);
}
