package com.toyshop.consultation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConsultationListItemResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private String productCoverUrl;
    private Long sellerId;
    private Integer status;
    private String lastMessage;
    private String lastSenderType;
    private Integer unreadUserCount;
    private Integer unreadAdminCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
