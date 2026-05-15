package com.toyshop.aftersale.service;

import com.toyshop.aftersale.entity.AfterSaleOrder;

import java.util.List;

public interface AfterSaleService {
    void apply(Long userId, Long orderId, String reason);

    List<AfterSaleOrder> userList(Long userId);

    List<AfterSaleOrder> adminList();

    void approve(Long afterSaleId, String reply);

    void reject(Long afterSaleId, String reply);
}
