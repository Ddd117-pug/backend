package com.toyshop.address.controller;

import com.toyshop.address.dto.AddressSaveRequest;
import com.toyshop.address.entity.UserAddress;
import com.toyshop.address.service.UserAddressService;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping("/list")
    public ApiResponse<List<UserAddress>> list(Authentication authentication) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(userAddressService.list(userId));
    }

    @GetMapping("/default")
    public ApiResponse<UserAddress> defaultAddress(Authentication authentication) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(userAddressService.getDefaultAddress(userId));
    }

    @PostMapping("/add")
    public ApiResponse<Long> add(Authentication authentication, @Valid @RequestBody AddressSaveRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(userAddressService.add(userId, request));
    }

    @PutMapping("/{addressId}")
    public ApiResponse<Void> update(Authentication authentication,
                                    @PathVariable Long addressId,
                                    @Valid @RequestBody AddressSaveRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        userAddressService.update(userId, addressId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long addressId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        userAddressService.delete(userId, addressId);
        return ApiResponse.success();
    }

    @PutMapping("/{addressId}/default")
    public ApiResponse<Void> setDefault(Authentication authentication, @PathVariable Long addressId) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        userAddressService.setDefault(userId, addressId);
        return ApiResponse.success();
    }
}
