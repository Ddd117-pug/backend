package com.toyshop.consultation.controller;

import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import com.toyshop.consultation.dto.ConsultationDetailResponse;
import com.toyshop.consultation.dto.ConsultationPageResponse;
import com.toyshop.consultation.dto.CreateConsultationRequest;
import com.toyshop.consultation.dto.ReplyConsultationRequest;
import com.toyshop.consultation.service.ConsultationService;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {
    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ApiResponse<Long> create(Authentication authentication, @RequestBody CreateConsultationRequest request) {
        JwtUser user = requireUser(authentication);
        return ApiResponse.success(consultationService.createConsultation(user.getUserId(), request));
    }

    @GetMapping("/my")
    public ApiResponse<ConsultationPageResponse> myPage(Authentication authentication,
                                                        @RequestParam(required = false) Integer status,
                                                        @RequestParam(required = false) Integer pageNum,
                                                        @RequestParam(required = false) Integer pageSize) {
        JwtUser user = requireUser(authentication);
        return ApiResponse.success(consultationService.myConsultationPage(user.getUserId(), status, pageNum, pageSize));
    }

    @GetMapping("/{consultationId}")
    public ApiResponse<ConsultationDetailResponse> detail(Authentication authentication, @PathVariable Long consultationId) {
        JwtUser user = requireUser(authentication);
        return ApiResponse.success(consultationService.myConsultationDetail(user.getUserId(), consultationId));
    }

    @PostMapping("/{consultationId}/messages")
    public ApiResponse<Void> sendMessage(Authentication authentication,
                                         @PathVariable Long consultationId,
                                         @RequestBody ReplyConsultationRequest request) {
        JwtUser user = requireUser(authentication);
        consultationService.sendMessage(user.getUserId(), consultationId, request.getContent());
        return ApiResponse.success();
    }

    @PostMapping("/{consultationId}/close")
    public ApiResponse<Void> close(Authentication authentication, @PathVariable Long consultationId) {
        JwtUser user = requireUser(authentication);
        consultationService.closeConsultation(user.getUserId(), consultationId);
        return ApiResponse.success();
    }

    private JwtUser requireUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        return (JwtUser) authentication.getPrincipal();
    }
}
