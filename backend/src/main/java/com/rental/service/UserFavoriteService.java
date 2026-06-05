package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.UserFavorite;
import com.rental.entity.Device;
import java.util.List;

public interface UserFavoriteService extends IService<UserFavorite> {
    boolean isFavorite(Long userId, Long deviceId);
    void toggleFavorite(Long userId, Long deviceId);
    List<Device> getUserFavorites(Long userId);
}
