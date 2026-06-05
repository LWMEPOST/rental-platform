package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.Device;
import com.rental.entity.UserFavorite;
import com.rental.mapper.DeviceMapper;
import com.rental.mapper.UserFavoriteMapper;
import com.rental.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public boolean isFavorite(Long userId, Long deviceId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getDeviceId, deviceId);
        return this.count(wrapper) > 0;
    }

    @Override
    public void toggleFavorite(Long userId, Long deviceId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getDeviceId, deviceId);
        
        UserFavorite favorite = this.getOne(wrapper);
        if (favorite != null) {
            // Already favorited, so remove it
            this.removeById(favorite.getId());
        } else {
            // Not favorited, so add it
            favorite = new UserFavorite();
            favorite.setUserId(userId);
            favorite.setDeviceId(deviceId);
            this.save(favorite);
        }
    }

    @Override
    public List<Device> getUserFavorites(Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .orderByDesc(UserFavorite::getCreateTime);
        
        List<UserFavorite> favorites = this.list(wrapper);
        if (favorites.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> deviceIds = favorites.stream()
                .map(UserFavorite::getDeviceId)
                .collect(Collectors.toList());
                
        return deviceMapper.selectBatchIds(deviceIds);
    }
}
