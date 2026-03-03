package com.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("device")
public class Device {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private java.util.List<Long> categoryIds;
    
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private java.util.List<String> categoryNames;

    private String name;
    private String brand;
    private String model;
    private String description;
    private String mainImage;
    private String detailImages; // JSON array
    private String specs; // JSON object
    private BigDecimal depositAmount;
    private BigDecimal rentalPrice;
    private Integer stockQuantity;
    private Integer status; // 1-On Shelf, 0-Off Shelf
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
