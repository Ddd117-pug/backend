package com.toyshop.consultation.dto;

import lombok.Data;

@Data
public class CreateConsultationRequest {
    private Long productId;
    private String content;
}
