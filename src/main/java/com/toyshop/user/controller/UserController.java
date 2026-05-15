package com.toyshop.user.controller;

import com.toyshop.common.response.ApiResponse;
import com.toyshop.security.JwtUser;
import com.toyshop.user.dto.ChangePasswordRequest;
import com.toyshop.user.dto.ForgotPasswordRequest;
import com.toyshop.user.dto.LoginRequest;
import com.toyshop.user.dto.LoginResponse;
import com.toyshop.user.dto.PointsExchangeRequest;
import com.toyshop.user.dto.RechargeRequest;
import com.toyshop.user.dto.ResetPasswordCodeResponse;
import com.toyshop.user.dto.ResetPasswordRequest;
import com.toyshop.user.dto.UpdateProfileRequest;
import com.toyshop.user.dto.UserProfileResponse;
import com.toyshop.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<Long> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<ResetPasswordCodeResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ApiResponse.success(userService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        return ApiResponse.success(userService.me(jwtUser.getUserId()));
    }

    @PutMapping("/me")
    public ApiResponse<Void> updateMe(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        userService.updateProfile(jwtUser.getUserId(), request);
        return ApiResponse.success();
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(Authentication authentication,
                                           @Valid @RequestBody ChangePasswordRequest request) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        userService.changePassword(jwtUser.getUserId(), request);
        return ApiResponse.success();
    }

    @PostMapping("/recharge")
    public ApiResponse<Void> recharge(Authentication authentication,
                                      @Valid @RequestBody RechargeRequest request) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        userService.recharge(jwtUser.getUserId(), request.getAmount());
        return ApiResponse.success();
    }

    @PostMapping("/points/exchange")
    public ApiResponse<Void> exchangePoints(Authentication authentication,
                                            @Valid @RequestBody PointsExchangeRequest request) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        userService.exchangePoints(jwtUser.getUserId(), request.getPoints());
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        userService.logout(jwtUser.getUserId());
        return ApiResponse.success();
    }

    @PostMapping("/cancel")
    public ApiResponse<Void> cancel(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        userService.cancelAccount(jwtUser.getUserId());
        return ApiResponse.success();
    }
}
