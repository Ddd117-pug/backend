package com.toyshop.review.service;

import com.toyshop.review.dto.AddReviewRequest;
import com.toyshop.review.dto.ReviewPageResponse;
import com.toyshop.review.entity.ProductReview;

import java.util.List;

public interface ReviewService {
    Long add(Long userId, AddReviewRequest request);

    List<ProductReview> listByProduct(Long productId);

    ReviewPageResponse adminList(Integer pageNum, Integer pageSize, Long productId, Integer status);

    void updateStatus(Long reviewId, Integer status);
}
