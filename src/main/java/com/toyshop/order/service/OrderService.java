package com.toyshop.order.service;

import com.toyshop.order.dto.AfterSaleRequest;
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

    void applyAfterSale(Long userId, Long orderId, AfterSaleRequest request);

    void approveAfterSale(Long afterSaleId);

    void rejectAfterSale(Long afterSaleId, String reply);

    java.util.List<com.toyshop.order.entity.ToyAfterSale> myAfterSales(Long userId);

    com.toyshop.product.dto.PageResponse<com.toyshop.order.entity.ToyAfterSale> afterSalePage(Integer pageNum, Integer pageSize, Integer status, Long userId, String orderNo);

    /**
     * 兼容旧管理员后台直接退款入口
     */
    void refund(Long orderId);
}
