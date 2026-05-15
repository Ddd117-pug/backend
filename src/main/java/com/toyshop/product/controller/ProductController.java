package com.toyshop.product.controller;

import com.toyshop.common.response.ApiResponse;
import com.toyshop.product.dto.PageResponse;
import com.toyshop.product.entity.Category;
import com.toyshop.product.entity.Product;
import com.toyshop.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/category/list")
    public ApiResponse<List<Category>> categoryList() {
        return ApiResponse.success(productService.categoryList());
    }

    @GetMapping("/product/list")
    public ApiResponse<PageResponse<Product>> productList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.success(productService.pageList(categoryId, brandId, pageNum, pageSize));
    }

    @GetMapping("/product/hot")
    public ApiResponse<List<Product>> hotList() {
        return ApiResponse.success(productService.hotList());
    }

    @GetMapping("/product/new")
    public ApiResponse<List<Product>> newList() {
        return ApiResponse.success(productService.newList());
    }

    @GetMapping("/product/detail/{id}")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        return ApiResponse.success(productService.detail(id));
    }
}
