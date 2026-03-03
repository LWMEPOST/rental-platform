package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.Device;
import com.rental.entity.DeviceCategory;
import com.rental.mapper.DeviceMapper;
import com.rental.service.DeviceCategoryService;
import com.rental.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DeviceCategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Device entity) {
        // 1. Save device
        boolean success = super.save(entity);
        if (success && entity.getCategoryIds() != null && !entity.getCategoryIds().isEmpty()) {
            // 2. Save mappings
            saveMappings(entity.getId(), entity.getCategoryIds());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Device entity) {
        // 1. Update device
        boolean success = super.updateById(entity);
        if (success) {
            // 2. Update mappings: delete old, insert new
            // Assuming categoryIds are passed. If null, maybe don't update? 
            // Usually form sends all selected IDs. If empty list, clear all.
            if (entity.getCategoryIds() != null) {
                String deleteSql = "DELETE FROM device_category_mapping WHERE device_id = ?";
                jdbcTemplate.update(deleteSql, entity.getId());
                if (!entity.getCategoryIds().isEmpty()) {
                    saveMappings(entity.getId(), entity.getCategoryIds());
                }
            }
        }
        return success;
    }

    @Override
    public Device getById(java.io.Serializable id) {
        Device device = super.getById(id);
        if (device != null) {
            fillCategories(List.of(device));
        }
        return device;
    }
    
    // Helper to batch fill categories
    public void fillCategories(List<Device> devices) {
        if (devices == null || devices.isEmpty()) return;
        
        List<Long> deviceIds = devices.stream().map(Device::getId).collect(Collectors.toList());
        if (deviceIds.isEmpty()) return;

        // Query all mappings for these devices
        // Select device_id, category_id, c.name 
        // FROM mapping m JOIN category c ON m.category_id = c.id
        // WHERE m.device_id IN (...)
        
        // Construct IN clause
        String inClause = deviceIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "SELECT m.device_id, m.category_id, c.name " +
                     "FROM device_category_mapping m " +
                     "LEFT JOIN device_category c ON m.category_id = c.id " +
                     "WHERE m.device_id IN (" + inClause + ")";
                     
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        
        // Group by deviceId
        Map<Long, List<Long>> idMap = rows.stream()
            .collect(Collectors.groupingBy(
                r -> ((Number) r.get("device_id")).longValue(),
                Collectors.mapping(r -> ((Number) r.get("category_id")).longValue(), Collectors.toList())
            ));
            
        Map<Long, List<String>> nameMap = rows.stream()
            .collect(Collectors.groupingBy(
                r -> ((Number) r.get("device_id")).longValue(),
                Collectors.mapping(r -> (String) r.get("name"), Collectors.toList())
            ));

        for (Device d : devices) {
            d.setCategoryIds(idMap.getOrDefault(d.getId(), new ArrayList<>()));
            d.setCategoryNames(nameMap.getOrDefault(d.getId(), new ArrayList<>()));
        }
    }

    private void saveMappings(Long deviceId, List<Long> categoryIds) {
        String sql = "INSERT INTO device_category_mapping (device_id, category_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Long cid : categoryIds) {
            batchArgs.add(new Object[]{deviceId, cid});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
