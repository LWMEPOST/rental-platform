package com.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_auth")
public class UserAuth {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String realName;
    private String idCard;
    private String cardFrontImg;
    private String cardBackImg;
    private Integer status; // 0-Pending, 1-Approved, 2-Rejected
    private String auditRemark;
    private LocalDateTime createTime;
    private LocalDateTime auditTime;
}
