package com.toyshop.aftersale.dto;

import lombok.Data;

@Data
public class AdminAfterSaleRow {
    private Long id;
    private Long orderId;
    private Long userId;
    private String orderNo;
    private String reason;
    private Integer status;
    private String reply;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String totalAmount;
    private String appliedAt;
    private String auditedAt;
    private String createdAt;
}
