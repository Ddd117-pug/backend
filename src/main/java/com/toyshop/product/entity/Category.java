package com.toyshop.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField("parent_id")
    private Long parentId;

    @TableField("sort_order")
    private Integer sortOrder;

    private Integer status;

    @TableField("icon_url")
    private String iconUrl;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
