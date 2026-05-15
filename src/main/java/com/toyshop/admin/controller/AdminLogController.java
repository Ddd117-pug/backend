package com.toyshop.admin.controller;

import com.toyshop.admin.dto.AdminOperationLogPageResponse;
import com.toyshop.admin.log.AdminOperationLog;
import com.toyshop.admin.service.AdminOperationLogService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {
    private final AdminOperationLogService adminOperationLogService;

    public AdminLogController(AdminOperationLogService adminOperationLogService) {
        this.adminOperationLogService = adminOperationLogService;
    }

    @GetMapping
    public ApiResponse<AdminOperationLogPageResponse> page(Authentication authentication,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) String module,
                                                           @RequestParam(required = false) String level,
                                                           @RequestParam(required = false) String startDate,
                                                           @RequestParam(required = false) String endDate,
                                                           @RequestParam(required = false) Integer pageNum,
                                                           @RequestParam(required = false) Integer pageSize) {
        requireAdmin(authentication);
        return ApiResponse.success(adminOperationLogService.page(keyword, module, level, startDate, endDate, pageNum, pageSize));
    }

    @PostMapping
    public ApiResponse<Void> create(Authentication authentication, @RequestBody AdminOperationLog log) {
        requireAdmin(authentication);
        adminOperationLogService.create(log);
        return ApiResponse.success();
    }

    private void requireAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUser jwtUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        if (jwtUser.getRole() == null || jwtUser.getRole() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅管理员可操作");
        }
    }
}
