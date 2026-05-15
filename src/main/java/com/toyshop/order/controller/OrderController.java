package com.toyshop.order.controller;

import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import com.toyshop.order.dto.CreateOrderRequest;
import com.toyshop.order.dto.OrderDetailResponse;
import com.toyshop.order.dto.OrderPageResponse;
import com.toyshop.order.dto.PayOrderRequest;
import com.toyshop.order.service.OrderService;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ApiResponse<Long> create(Authentication authentication, @Valid @RequestBody CreateOrderRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(orderService.create(userId, request));
    }

    /**
     * 支付模拟：待支付 -> 已支付
     */
    @PostMapping("/{orderId}/pay")
    public ApiResponse<Void> pay(Authentication authentication,
                                 @PathVariable Long orderId,
                                 @Valid @RequestBody PayOrderRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        orderService.pay(userId, orderId, request.getPayType());
        return ApiResponse.success();
    }

    @GetMapping("/list")
    public ApiResponse<OrderPageResponse> list(Authentication authentication,
                                               @RequestParam(required = false) Integer pageNum,
                                               @RequestParam(required = false) Integer pageSize) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(orderService.list(userId, pageNum, pageSize));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> detail(Authentication authentication, @PathVariable Long orderId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(orderService.detail(userId, orderId));
    }

    /**
     * 取消订单：待支付 -> 已取消
     */
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancel(Authentication authentication, @PathVariable Long orderId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        orderService.cancel(userId, orderId);
        return ApiResponse.success();
    }

    /**
     * 发货：已支付 -> 已发货（管理员）
     */
    @PostMapping("/{orderId}/ship")
    public ApiResponse<Void> ship(Authentication authentication, @PathVariable Long orderId) {
        requireAdmin(authentication);
        orderService.ship(orderId);
        return ApiResponse.success();
    }

    /**
     * 确认收货：已发货 -> 已完成
     */
    @PostMapping("/{orderId}/receive")
    public ApiResponse<Void> receive(Authentication authentication, @PathVariable Long orderId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        orderService.receive(userId, orderId);
        return ApiResponse.success();
    }

    @PostMapping("/{orderId}/refund")
    public ApiResponse<Void> refund(Authentication authentication, @PathVariable Long orderId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        orderService.refund(userId, orderId);
        return ApiResponse.success();
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
