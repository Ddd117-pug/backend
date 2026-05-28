package com.toyshop.consultation.service;

import com.toyshop.consultation.dto.ConsultationDetailResponse;
import com.toyshop.consultation.dto.ConsultationPageResponse;
import com.toyshop.consultation.dto.CreateConsultationRequest;
import com.toyshop.consultation.dto.ReplyConsultationRequest;

public interface ConsultationService {
    Long createConsultation(Long userId, CreateConsultationRequest request);

    ConsultationPageResponse myConsultationPage(Long userId, Integer status, Integer pageNum, Integer pageSize);

    ConsultationDetailResponse myConsultationDetail(Long userId, Long consultationId);

    void sendMessage(Long userId, Long consultationId, String content);

    void closeConsultation(Long userId, Long consultationId);

    ConsultationPageResponse adminConsultationPage(String keyword, Long userId, Long productId, Integer status, Integer pageNum, Integer pageSize);

    ConsultationDetailResponse adminConsultationDetail(Long consultationId);

    void adminReply(Long adminUserId, Long consultationId, ReplyConsultationRequest request);

    void adminClose(Long consultationId);

    void adminMarkRead(Long consultationId);

    void markUserRead(Long userId, Long consultationId);
}
