package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.RentalComment;

import java.util.List;

public interface RentalCommentService extends IService<RentalComment> {
    void addComment(RentalComment comment);
    List<RentalComment> getByDeviceId(Long deviceId);
}
