package com.toyshop.order.service;

import com.toyshop.order.dto.CreateOrderRequest;
import com.toyshop.order.dto.OrderDetailResponse;
import com.toyshop.order.dto.OrderPageResponse;

public interface OrderService {
    Long create(Long userId, CreateOrderRequest request);

    void pay(Long userId, Long orderId, Integer payType);

    OrderPageResponse list(Long userId, Integer pageNum, Integer pageSize);

    OrderDetailResponse detail(Long userId, Long orderId);

    void cancel(Long userId, Long orderId);

    void ship(Long orderId);

    void receive(Long userId, Long orderId);

    void refund(Long userId, Long orderId);

    void refund(Long orderId);
}
