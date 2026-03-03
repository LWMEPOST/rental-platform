package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.DeviceCategory;
import java.util.List;
import java.util.Map;

public interface DeviceCategoryService extends IService<DeviceCategory> {
    List<Map<String, Object>> getCategoryDeviceCounts();
}
