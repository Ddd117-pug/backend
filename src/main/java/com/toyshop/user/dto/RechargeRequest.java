package com.toyshop.user.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RechargeRequest {
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    @DecimalMax(value = "2000.00", message = "单次充值金额不能超过2000")
    private BigDecimal amount;
}
