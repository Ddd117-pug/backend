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
@TableName("toy_order")
public class ToyOrder {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("order_no")
    private String orderNo;

    /**
     * 订单总金额（含运费）
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 商品总金额
     */
    @TableField("product_amount")
    private BigDecimal productAmount;

    /**
     * 运费金额
     */
    @TableField("freight_amount")
    private BigDecimal freightAmount;

    /**
     * 状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
     */
    private Integer status;

    /**
     * 支付方式：0-未选择 1-微信 2-支付宝等
     */
    @TableField("pay_type")
    private Integer payType;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("close_time")
    private LocalDateTime closeTime;

    @TableField("finish_time")
    private LocalDateTime finishTime;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("receiver_address")
    private String receiverAddress;

    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

