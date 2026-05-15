package com.toyshop.order.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PayOrderRequest {
    @NotNull(message = "支付方式不能为空")
    @Min(value = 0, message = "支付方式不正确")
    @Max(value = 2, message = "支付方式不正确")
    private Integer payType;
}
