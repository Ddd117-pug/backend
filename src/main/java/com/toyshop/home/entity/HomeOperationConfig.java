package com.toyshop.home.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("home_operation_config")
public class HomeOperationConfig {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String type;

    private String title;

    private String subtitle;

    private String imageUrl;

    private String linkUrl;

    private String targetType;

    private String targetValue;

    private Integer sortOrder;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
