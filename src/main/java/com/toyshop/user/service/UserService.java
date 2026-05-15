package com.toyshop.user.service;

import com.toyshop.user.controller.RegisterRequest;
import com.toyshop.user.dto.ChangePasswordRequest;
import com.toyshop.user.dto.ForgotPasswordRequest;
import com.toyshop.user.dto.LoginRequest;
import com.toyshop.user.dto.LoginResponse;
import com.toyshop.user.dto.ResetPasswordCodeResponse;
import com.toyshop.user.dto.ResetPasswordRequest;
import com.toyshop.user.dto.UpdateProfileRequest;
import com.toyshop.user.dto.UserProfileResponse;

public interface UserService {
    Long register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    ResetPasswordCodeResponse forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    UserProfileResponse me(Long userId);

    void updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    void recharge(Long userId, java.math.BigDecimal amount);

    void exchangePoints(Long userId, Integer points);

    void logout(Long userId);

    void cancelAccount(Long userId);
}
