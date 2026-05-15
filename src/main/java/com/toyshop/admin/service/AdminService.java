package com.toyshop.admin.service;

import com.toyshop.admin.dto.AdminStatsResponse;
import com.toyshop.admin.dto.DailyTrendPoint;
import com.toyshop.aftersale.dto.AdminAfterSaleRow;
import com.toyshop.order.dto.OrderDetailResponse;
import com.toyshop.order.entity.ToyOrder;
import com.toyshop.product.dto.PageResponse;
import com.toyshop.product.entity.Brand;
import com.toyshop.product.entity.Category;
import com.toyshop.product.entity.Product;
import com.toyshop.review.dto.ReviewPageResponse;
import com.toyshop.user.entity.SysUser;
import com.toyshop.address.entity.UserAddress;

import java.util.List;

public interface AdminService {
    PageResponse<Product> productPage(String keyword, Long categoryId, Integer status, Integer pageNum, Integer pageSize);
    Product productDetail(Long id);
    Long addProduct(Product product);
    PageResponse<Brand> brandPage(String keyword, Integer status, Integer featured, Integer pageNum, Integer pageSize);
    Brand brandDetail(Long id);
    Long addBrand(Brand brand);
    void updateBrand(Long id, Brand brand);
    void deleteBrand(Long id);
    PageResponse<Product> brandProducts(Long brandId, Integer pageNum, Integer pageSize);
    SysUser userDetailWithMeta(Long userId);
    java.util.List<java.util.Map<String, Object>> userLoginRecords(Long userId);
    PageResponse<ToyOrder> userRecentOrders(Long userId, Integer pageNum, Integer pageSize);
    java.util.List<UserAddress> userAddresses(Long userId);
    SysUser userMeta(Long userId);
    void updateProduct(Long id, Product product);
    void updateProductStatus(Long id, Integer status);
    void deleteProduct(Long id);
    List<Category> categoryList();
    Long addCategory(Category category);
    void updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    PageResponse<ToyOrder> orderPage(String orderNo, Long userId, Integer status, Integer pageNum, Integer pageSize);
    OrderDetailResponse orderDetail(Long orderId);
    void shipOrder(Long orderId);
    void cancelOrder(Long orderId);
    void refundOrder(Long orderId);
    List<AdminAfterSaleRow> afterSaleList();
    void approveAfterSale(Long afterSaleId, String reply);
    void rejectAfterSale(Long afterSaleId, String reply);
    PageResponse<SysUser> userPage(String keyword, Integer status, Integer pageNum, Integer pageSize);
    SysUser userDetail(Long userId);
    void updateUserStatus(Long userId, Integer status);
    void updateUserRole(Long userId, Integer role);
    ReviewPageResponse reviewPage(Integer pageNum, Integer pageSize, Long productId, Integer status);
    AdminStatsResponse stats();
    List<DailyTrendPoint> trend7d();
}
