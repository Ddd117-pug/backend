package com.toyshop.order.dto;

import com.toyshop.order.entity.ToyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderPageResponse {
    private long pageNum;
    private long pageSize;
    private long total;
    private List<ToyOrder> records;
}

