package com.toyshop.home.controller;

import com.toyshop.admin.controller.AdminController;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.home.dto.HomeOperationConfigRequest;
import com.toyshop.home.entity.HomeOperationConfig;
import com.toyshop.home.service.HomeOperationConfigService;
import com.toyshop.security.JwtUser;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HomeOperationConfigController {
    private final HomeOperationConfigService service;

    public HomeOperationConfigController(HomeOperationConfigService service) {
        this.service = service;
    }

    @GetMapping("/home/config")
    public ApiResponse<List<HomeOperationConfig>> publicList() {
        return ApiResponse.success(service.listActive());
    }

    @GetMapping("/admin/home-config")
    public ApiResponse<List<HomeOperationConfig>> adminList(Authentication a) {
        requireAdmin(a);
        return ApiResponse.success(service.listAll());
    }

    @PostMapping("/admin/home-config")
    public ApiResponse<Long> create(Authentication a, @RequestBody HomeOperationConfigRequest request) {
        requireAdmin(a);
        HomeOperationConfig config = toEntity(request);
        return ApiResponse.success(service.create(config));
    }

    @PutMapping("/admin/home-config/{id}")
    public ApiResponse<Void> update(Authentication a, @PathVariable Long id, @RequestBody HomeOperationConfigRequest request) {
        requireAdmin(a);
        service.update(id, toEntity(request));
        return ApiResponse.success();
    }

    @DeleteMapping("/admin/home-config/{id}")
    public ApiResponse<Void> delete(Authentication a, @PathVariable Long id) {
        requireAdmin(a);
        service.delete(id);
        return ApiResponse.success();
    }

    private HomeOperationConfig toEntity(HomeOperationConfigRequest request) {
        HomeOperationConfig config = new HomeOperationConfig();
        if (request != null) {
            config.setType(request.getType());
            config.setTitle(request.getTitle());
            config.setSubtitle(request.getSubtitle());
            config.setImageUrl(request.getImageUrl());
            config.setLinkUrl(request.getLinkUrl());
            config.setTargetType(request.getTargetType());
            config.setTargetValue(request.getTargetValue());
            config.setSortOrder(request.getSortOrder());
            config.setStatus(request.getStatus());
        }
        return config;
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
