package com.toyshop.cart.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteCartRequest {
    @NotNull(message = "购物车ID不能为空")
    private Long id;
}
