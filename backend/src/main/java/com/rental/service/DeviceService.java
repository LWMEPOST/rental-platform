package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.Device;
import java.util.List;

public interface DeviceService extends IService<Device> {
    void fillCategories(List<Device> devices);
}
