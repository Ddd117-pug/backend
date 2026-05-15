package com.toyshop.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("toy_order_item")
public class ToyOrderItem {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("product_id")
    private Long productId;

    @TableField("product_name")
    private String productName;

    @TableField("product_pic")
    private String productPic;

    private BigDecimal price;

    private Integer quantity;

    @TableField("style_option")
    private String styleOption;

    /**
     * amount = price * quantity
     */
    private BigDecimal amount;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
