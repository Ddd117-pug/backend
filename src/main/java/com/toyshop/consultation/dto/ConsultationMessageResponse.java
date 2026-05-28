package com.toyshop.consultation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConsultationMessageResponse {
    private Long id;
    private Long consultationId;
    private String senderType;
    private Long senderId;
    private String content;
    private String messageType;
    private Integer isRead;
    private LocalDateTime createdAt;
}
