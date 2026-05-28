package com.toyshop.consultation.dto;

import com.toyshop.product.entity.Product;
import com.toyshop.user.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConsultationDetailResponse {
    private Long consultationId;
    private Long userId;
    private Long productId;
    private Long sellerId;
    private Integer status;
    private String lastMessage;
    private String lastSenderType;
    private Integer unreadUserCount;
    private Integer unreadAdminCount;
    private Product product;
    private SysUser user;
    private List<ConsultationMessageResponse> messages;
}
