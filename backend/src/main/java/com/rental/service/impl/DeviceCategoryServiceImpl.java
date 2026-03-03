package com.rental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.DeviceCategory;
import com.rental.mapper.DeviceCategoryMapper;
import com.rental.service.DeviceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeviceCategoryServiceImpl extends ServiceImpl<DeviceCategoryMapper, DeviceCategory> implements DeviceCategoryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getCategoryDeviceCounts() {
        String sql = "SELECT c.name as name, COUNT(m.device_id) as value " +
                     "FROM device_category c " +
                     "LEFT JOIN device_category_mapping m ON c.id = m.category_id " +
                     "GROUP BY c.id, c.name";
        return jdbcTemplate.queryForList(sql);
    }
}
