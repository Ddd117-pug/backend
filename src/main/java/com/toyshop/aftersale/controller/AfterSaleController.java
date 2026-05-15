package com.toyshop.aftersale.controller;

import com.toyshop.aftersale.dto.ApplyAfterSaleRequest;
import com.toyshop.aftersale.entity.AfterSaleOrder;
import com.toyshop.aftersale.service.AfterSaleService;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/after-sale")
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    public AfterSaleController(AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    @PostMapping("/{orderId}/apply")
    public ApiResponse<Void> apply(Authentication authentication,
                                   @PathVariable Long orderId,
                                   @Valid @RequestBody ApplyAfterSaleRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        afterSaleService.apply(userId, orderId, request.getReason());
        return ApiResponse.success();
    }

    @GetMapping("/my")
    public ApiResponse<List<AfterSaleOrder>> my(Authentication authentication) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(afterSaleService.userList(userId));
    }
}
