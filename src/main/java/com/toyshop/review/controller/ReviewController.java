package com.toyshop.review.controller;

import com.toyshop.common.response.ApiResponse;
import com.toyshop.review.dto.AddReviewRequest;
import com.toyshop.review.entity.ProductReview;
import com.toyshop.review.service.ReviewService;
import com.toyshop.security.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 添加评价（用户）
     */
    @PostMapping("/add")
    public ApiResponse<Long> add(Authentication authentication, @Valid @RequestBody AddReviewRequest request) {
        Long userId = ((JwtUser) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(reviewService.add(userId, request));
    }

    /**
     * 查询某商品评价（前台）
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductReview>> listByProduct(@PathVariable Long productId) {
        return ApiResponse.success(reviewService.listByProduct(productId));
    }
}

