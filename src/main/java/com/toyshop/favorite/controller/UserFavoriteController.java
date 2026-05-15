package com.toyshop.favorite.controller;

import com.toyshop.common.response.ApiResponse;
import com.toyshop.favorite.dto.FavoriteProductResponse;
import com.toyshop.favorite.service.UserFavoriteService;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    @PostMapping("/{productId}")
    public ApiResponse<Void> add(Authentication authentication, @PathVariable Long productId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        userFavoriteService.add(userId, productId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> remove(Authentication authentication, @PathVariable Long productId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        userFavoriteService.remove(userId, productId);
        return ApiResponse.success();
    }

    @GetMapping("/list")
    public ApiResponse<List<FavoriteProductResponse>> list(Authentication authentication) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(userFavoriteService.list(userId));
    }
}
