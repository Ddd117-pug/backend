package com.toyshop.cart.controller;

import com.toyshop.cart.dto.AddCartRequest;
import com.toyshop.cart.dto.CartItemVO;
import com.toyshop.cart.dto.DeleteCartRequest;
import com.toyshop.cart.dto.UpdateCartRequest;
import com.toyshop.cart.service.CartService;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ApiResponse<Long> add(Authentication authentication, @Valid @RequestBody AddCartRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(cartService.add(userId, request));
    }

    @GetMapping("/list")
    public ApiResponse<List<CartItemVO>> list(Authentication authentication) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(cartService.list(userId));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(Authentication authentication, @Valid @RequestBody UpdateCartRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        cartService.update(userId, request.getId(), request.getNum());
        return ApiResponse.success();
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(Authentication authentication, @Valid @RequestBody DeleteCartRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        cartService.delete(userId, request.getId());
        return ApiResponse.success();
    }
}
