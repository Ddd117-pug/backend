package com.toyshop.order.entity;

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
@TableName("toy_after_sale")
public class ToyAfterSale {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("order_no")
    private String orderNo;

    private String reason;

    /**
     * 0-待审核 1-已通过 2-已驳回 3-已退款
     */
    private Integer status;

    private String reply;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("audited_at")
    private LocalDateTime auditedAt;
}
