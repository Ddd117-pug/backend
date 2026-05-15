package com.toyshop.user.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PointsExchangeRequest {
    @NotNull(message = "兑换积分不能为空")
    @Min(value = 100, message = "至少需要兑换 100 积分")
    private Integer points;
}
