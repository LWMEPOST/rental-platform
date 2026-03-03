package com.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rental_comment")
public class RentalComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username; // Redundant but useful for display
    private Long orderId;
    private Long deviceId;
    private String content;
    private Integer rating; // 1-5 stars
    private LocalDateTime createTime;
}
