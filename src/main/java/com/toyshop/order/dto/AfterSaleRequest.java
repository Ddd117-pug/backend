package com.toyshop.order.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AfterSaleRequest {
    @NotBlank(message = "请填写售后原因")
    private String reason;
}
