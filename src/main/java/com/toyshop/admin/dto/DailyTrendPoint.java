package com.toyshop.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DailyTrendPoint {
    private String date;
    private Long orderCount;
    private BigDecimal salesAmount;
}

