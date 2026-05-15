package com.toyshop.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("inventory")
public class Inventory {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("total_stock")
    private Integer totalStock;

    @TableField("locked_stock")
    private Integer lockedStock;

    private String warehouse;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;
}

