package com.toyshop.admin.controller.stats;

import com.toyshop.admin.dto.AdminStatsResponse;
import com.toyshop.admin.dto.DailyTrendPoint;
import com.toyshop.admin.dto.OrderStatusStat;
import com.toyshop.admin.dto.RankItem;
import com.toyshop.admin.service.AdminService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {
    private final AdminService adminService;

    public AdminStatsController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/overview")
    public ApiResponse<AdminStatsResponse> overview(Authentication authentication) {
        requireAdmin(authentication);
        return ApiResponse.success(adminService.stats());
    }

    @GetMapping("/trend7d")
    public ApiResponse<List<DailyTrendPoint>> trend7d(Authentication authentication) {
        requireAdmin(authentication);
        return ApiResponse.success(adminService.trend7d());
    }

    @GetMapping("/order-status")
    public ApiResponse<List<OrderStatusStat>> orderStatus(Authentication authentication) {
        requireAdmin(authentication);
        return ApiResponse.success(adminService.orderStatusStats());
    }

    @GetMapping("/brand-sales")
    public ApiResponse<List<RankItem>> brandSales(Authentication authentication) {
        requireAdmin(authentication);
        return ApiResponse.success(adminService.brandSalesRank());
    }

    @GetMapping("/hot-products")
    public ApiResponse<List<RankItem>> hotProducts(Authentication authentication) {
        requireAdmin(authentication);
        return ApiResponse.success(adminService.hotProductRank());
    }

    private void requireAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        if (jwtUser.getRole() == null || jwtUser.getRole() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅管理员可操作");
        }
    }
}
