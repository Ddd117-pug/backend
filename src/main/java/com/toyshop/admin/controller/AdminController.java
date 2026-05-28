package com.toyshop.admin.controller;

import com.toyshop.admin.service.AdminService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import com.toyshop.order.dto.OrderDetailResponse;
import com.toyshop.order.entity.ToyAfterSale;
import com.toyshop.order.entity.ToyOrder;
import com.toyshop.order.service.OrderService;
import com.toyshop.product.dto.PageResponse;
import com.toyshop.product.entity.Brand;
import com.toyshop.product.entity.Category;
import com.toyshop.product.entity.Product;
import com.toyshop.review.dto.ReviewPageResponse;
import com.toyshop.review.service.ReviewService;
import com.toyshop.security.JwtUser;
import com.toyshop.user.entity.SysUser;
import com.toyshop.address.entity.UserAddress;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    public AdminController(AdminService adminService, ReviewService reviewService, OrderService orderService) {
        this.adminService = adminService;
        this.reviewService = reviewService;
        this.orderService = orderService;
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }

    @GetMapping("/products")
    public ApiResponse<PageResponse<Product>> products(Authentication a,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long categoryId,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(required = false) Integer pageNum,
                                                       @RequestParam(required = false) Integer pageSize) {
        requireAdmin(a);
        return ApiResponse.success(adminService.productPage(keyword, categoryId, status, pageNum, pageSize));
    }

    @GetMapping("/brands")
    public ApiResponse<PageResponse<Brand>> brands(Authentication a,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(required = false) Integer featured,
                                                   @RequestParam(required = false) Integer pageNum,
                                                   @RequestParam(required = false) Integer pageSize) {
        requireAdmin(a);
        return ApiResponse.success(adminService.brandPage(keyword, status, featured, pageNum, pageSize));
    }

    @GetMapping("/brands/{id}")
    public ApiResponse<Brand> brandDetail(Authentication a, @PathVariable Long id) {
        requireAdmin(a);
        return ApiResponse.success(adminService.brandDetail(id));
    }

    @PostMapping("/brands")
    public ApiResponse<Long> addBrand(Authentication a, @RequestBody Brand brand) {
        requireAdmin(a);
        return ApiResponse.success(adminService.addBrand(brand));
    }

    @PutMapping("/brands/{id}")
    public ApiResponse<Void> updateBrand(Authentication a, @PathVariable Long id, @RequestBody Brand brand) {
        requireAdmin(a);
        adminService.updateBrand(id, brand);
        return ApiResponse.success();
    }

    @DeleteMapping("/brands/{id}")
    public ApiResponse<Void> deleteBrand(Authentication a, @PathVariable Long id) {
        requireAdmin(a);
        adminService.deleteBrand(id);
        return ApiResponse.success();
    }

    @GetMapping("/brands/products")
    public ApiResponse<PageResponse<Product>> brandProducts(Authentication a,
                                                            @RequestParam Long brandId,
                                                            @RequestParam(required = false) Integer pageNum,
                                                            @RequestParam(required = false) Integer pageSize) {
        requireAdmin(a);
        return ApiResponse.success(adminService.brandProducts(brandId, pageNum, pageSize));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<Product> productDetail(Authentication a, @PathVariable Long id) {
        requireAdmin(a);
        return ApiResponse.success(adminService.productDetail(id));
    }

    @PostMapping("/product/add")
    public ApiResponse<Long> addProduct(Authentication a, @RequestBody Product p) {
        requireAdmin(a);
        return ApiResponse.success(adminService.addProduct(p));
    }

    @PutMapping("/products/{id}")
    public ApiResponse<Void> updateProduct(Authentication a, @PathVariable Long id, @RequestBody Product p) {
        requireAdmin(a);
        adminService.updateProduct(id, p);
        return ApiResponse.success();
    }

    @PutMapping("/product/{id}/status")
    public ApiResponse<Void> updateProductStatus(Authentication a, @PathVariable Long id, @RequestParam Integer status) {
        requireAdmin(a);
        adminService.updateProductStatus(id, status);
        return ApiResponse.success();
    }

    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(Authentication a, @PathVariable Long id) {
        requireAdmin(a);
        adminService.deleteProduct(id);
        return ApiResponse.success();
    }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> categories(Authentication a) {
        requireAdmin(a);
        return ApiResponse.success(adminService.categoryList());
    }

    @PostMapping("/categories")
    public ApiResponse<Long> addCategory(Authentication a, @RequestBody Category c) {
        requireAdmin(a);
        return ApiResponse.success(adminService.addCategory(c));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<Void> updateCategory(Authentication a, @PathVariable Long id, @RequestBody Category c) {
        requireAdmin(a);
        adminService.updateCategory(id, c);
        return ApiResponse.success();
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(Authentication a, @PathVariable Long id) {
        requireAdmin(a);
        adminService.deleteCategory(id);
        return ApiResponse.success();
    }

    @GetMapping("/orders")
    public ApiResponse<PageResponse<ToyOrder>> orders(Authentication a,
                                                      @RequestParam(required = false) String orderNo,
                                                      @RequestParam(required = false) Long userId,
                                                      @RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) Integer pageNum,
                                                      @RequestParam(required = false) Integer pageSize) {
        requireAdmin(a);
        return ApiResponse.success(adminService.orderPage(orderNo, userId, status, pageNum, pageSize));
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderDetailResponse> orderDetail(Authentication a, @PathVariable Long orderId) {
        requireAdmin(a);
        return ApiResponse.success(adminService.orderDetail(orderId));
    }

    @PostMapping("/orders/{orderId}/ship")
    public ApiResponse<Void> ship(Authentication a, @PathVariable Long orderId) {
        requireAdmin(a);
        adminService.shipOrder(orderId);
        return ApiResponse.success();
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(Authentication a, @PathVariable Long orderId) {
        requireAdmin(a);
        adminService.cancelOrder(orderId);
        return ApiResponse.success();
    }

    @GetMapping("/after-sale")
    public ApiResponse<com.toyshop.product.dto.PageResponse<ToyAfterSale>> afterSaleList(Authentication a,
                                                                                           @RequestParam(required = false) Integer pageNum,
                                                                                           @RequestParam(required = false) Integer pageSize,
                                                                                           @RequestParam(required = false) Integer status,
                                                                                           @RequestParam(required = false) Long userId,
                                                                                           @RequestParam(required = false) String orderNo) {
        requireAdmin(a);
        return ApiResponse.success(orderService.afterSalePage(pageNum, pageSize, status, userId, orderNo));
    }

    @PostMapping("/after-sale/{afterSaleId}/approve")
    public ApiResponse<Void> approveAfterSale(Authentication a,
                                              @PathVariable Long afterSaleId) {
        requireAdmin(a);
        orderService.approveAfterSale(afterSaleId);
        return ApiResponse.success();
    }

    @PostMapping("/after-sale/{afterSaleId}/reject")
    public ApiResponse<Void> rejectAfterSale(Authentication a,
                                             @PathVariable Long afterSaleId,
                                             @RequestParam(required = false) String reply) {
        requireAdmin(a);
        orderService.rejectAfterSale(afterSaleId, reply);
        return ApiResponse.success();
    }

    @GetMapping("/users")
    public ApiResponse<PageResponse<SysUser>> users(Authentication a,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(required = false) Integer pageNum,
                                                    @RequestParam(required = false) Integer pageSize) {
        requireAdmin(a);
        return ApiResponse.success(adminService.userPage(keyword, status, pageNum, pageSize));
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<SysUser> userDetail(Authentication a, @PathVariable Long userId) {
        requireAdmin(a);
        return ApiResponse.success(adminService.userDetail(userId));
    }

    @GetMapping("/users/{userId}/detail")
    public ApiResponse<SysUser> userDetailWithMeta(Authentication a, @PathVariable Long userId) {
        requireAdmin(a);
        return ApiResponse.success(adminService.userDetailWithMeta(userId));
    }

    @GetMapping("/users/{userId}/meta")
    public ApiResponse<SysUser> userMeta(Authentication a, @PathVariable Long userId) {
        requireAdmin(a);
        return ApiResponse.success(adminService.userMeta(userId));
    }

    @GetMapping("/users/{userId}/login-records")
    public ApiResponse<java.util.List<java.util.Map<String, Object>>> userLoginRecords(Authentication a, @PathVariable Long userId) {
        requireAdmin(a);
        return ApiResponse.success(adminService.userLoginRecords(userId));
    }

    @GetMapping("/users/{userId}/recent-orders")
    public ApiResponse<PageResponse<ToyOrder>> userRecentOrders(Authentication a, @PathVariable Long userId,
                                                               @RequestParam(required = false) Integer pageNum,
                                                               @RequestParam(required = false) Integer pageSize) {
        requireAdmin(a);
        return ApiResponse.success(adminService.userRecentOrders(userId, pageNum, pageSize));
    }

    @GetMapping("/users/{userId}/addresses")
    public ApiResponse<java.util.List<UserAddress>> userAddresses(Authentication a, @PathVariable Long userId) {
        requireAdmin(a);
        return ApiResponse.success(adminService.userAddresses(userId));
    }

    @PutMapping("/users/{userId}/status")
    public ApiResponse<Void> updateUserStatus(Authentication a, @PathVariable Long userId, @RequestParam Integer status) {
        requireAdmin(a);
        adminService.updateUserStatus(userId, status);
        return ApiResponse.success();
    }

    @PutMapping("/users/{userId}/role")
    public ApiResponse<Void> updateUserRole(Authentication a, @PathVariable Long userId, @RequestParam Integer role) {
        requireAdmin(a);
        adminService.updateUserRole(userId, role);
        return ApiResponse.success();
    }

    @GetMapping("/reviews")
    public ApiResponse<ReviewPageResponse> reviews(Authentication a,
                                                   @RequestParam(required = false) Integer pageNum,
                                                   @RequestParam(required = false) Integer pageSize,
                                                   @RequestParam(required = false) Long productId,
                                                   @RequestParam(required = false) Integer status) {
        requireAdmin(a);
        return ApiResponse.success(adminService.reviewPage(pageNum, pageSize, productId, status));
    }

    @PutMapping("/reviews/{reviewId}/status")
    public ApiResponse<Void> updateReviewStatus(Authentication a, @PathVariable Long reviewId, @RequestParam Integer status) {
        requireAdmin(a);
        reviewService.updateStatus(reviewId, status);
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
