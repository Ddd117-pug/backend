package com.toyshop.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AdminStatsResponse {
    private Long userCount;
    private Long productCount;
    private Long orderCount;
    private Long paidOrderCount;
    private BigDecimal paidOrderAmount;
}

