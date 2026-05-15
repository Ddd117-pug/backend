package com.toyshop.order.dto;

import com.toyshop.order.entity.ToyOrder;
import com.toyshop.order.entity.ToyOrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailResponse {
    private ToyOrder order;
    private List<ToyOrderItem> items;
}

