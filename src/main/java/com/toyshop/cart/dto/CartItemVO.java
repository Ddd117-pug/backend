package com.toyshop.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String coverUrl;
    private BigDecimal price;
    private String styleOption;
    private Integer num;
    private BigDecimal totalPrice;
    private String material;
    private String size;
}
