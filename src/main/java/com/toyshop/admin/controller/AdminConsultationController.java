package com.toyshop.admin.controller;

import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import com.toyshop.consultation.dto.ConsultationDetailResponse;
import com.toyshop.consultation.dto.ConsultationPageResponse;
import com.toyshop.consultation.dto.ReplyConsultationRequest;
import com.toyshop.consultation.service.ConsultationService;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/consultations")
public class AdminConsultationController {
    private final ConsultationService consultationService;

    public AdminConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @GetMapping
    public ApiResponse<ConsultationPageResponse> page(Authentication authentication,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Long userId,
                                                      @RequestParam(required = false) Long productId,
                                                      @RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) Integer pageNum,
                                                      @RequestParam(required = false) Integer pageSize) {
        requireAdmin(authentication);
        return ApiResponse.success(consultationService.adminConsultationPage(keyword, userId, productId, status, pageNum, pageSize));
    }

    @GetMapping("/{consultationId}")
    public ApiResponse<ConsultationDetailResponse> detail(Authentication authentication, @PathVariable Long consultationId) {
        requireAdmin(authentication);
        return ApiResponse.success(consultationService.adminConsultationDetail(consultationId));
    }

    @PostMapping("/{consultationId}/reply")
    public ApiResponse<Void> reply(Authentication authentication,
                                   @PathVariable Long consultationId,
                                   @RequestBody ReplyConsultationRequest request) {
        JwtUser admin = requireAdmin(authentication);
        consultationService.adminReply(admin.getUserId(), consultationId, request);
        return ApiResponse.success();
    }

    @PostMapping("/{consultationId}/close")
    public ApiResponse<Void> close(Authentication authentication, @PathVariable Long consultationId) {
        requireAdmin(authentication);
        consultationService.adminClose(consultationId);
        return ApiResponse.success();
    }

    @PostMapping("/{consultationId}/read")
    public ApiResponse<Void> markRead(Authentication authentication, @PathVariable Long consultationId) {
        requireAdmin(authentication);
        consultationService.adminMarkRead(consultationId);
        return ApiResponse.success();
    }

    private JwtUser requireAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        if (jwtUser.getRole() == null || jwtUser.getRole() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅管理员可操作");
        }
        return jwtUser;
    }
}
