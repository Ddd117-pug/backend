package com.toyshop.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("brand")
public class Brand {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String logo;

    private String description;

    @TableField(exist = false)
    private Integer productCount;

    @TableField(exist = false)
    private Integer featured;

    @TableField(exist = false)
    private Integer hot;

    @TableField(exist = false)
    private String initial;

    @TableField(exist = false)
    private String bannerUrl;

    @TableField(exist = false)
    private String nameSort;

    @TableField("sort_order")
    private Integer sortOrder;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
