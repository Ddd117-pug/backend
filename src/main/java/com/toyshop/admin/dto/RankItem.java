package com.toyshop.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankItem {
    private Long id;
    private String name;
    private BigDecimal value;
}
