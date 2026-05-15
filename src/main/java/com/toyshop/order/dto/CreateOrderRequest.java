package com.toyshop.order.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    /**
     * 支付方式：1-微信 2-支付宝
     */
    private Integer payType;

    private String remark;

    private Long productId;

    @Min(value = 1, message = "购买数量必须大于0")
    private Integer num;

    private String styleOption;

    private List<Long> cartIds;
}
