package com.toyshop.aftersale.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApplyAfterSaleRequest {
    @NotBlank(message = "售后原因不能为空")
    private String reason;
}
