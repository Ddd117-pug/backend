package com.toyshop.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long userCount;
    private long productCount;
    private long orderCount;
    private long paidOrderCount;
    private BigDecimal paidOrderAmount;
    private List<OrderStatusStat> orderStatusStats;
    private List<RankItem> brandSalesRank;
    private List<RankItem> hotProductRank;
}
