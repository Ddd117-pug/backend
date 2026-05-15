package com.toyshop.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.product.entity.Product;
import com.toyshop.product.mapper.ProductMapper;
import com.toyshop.review.dto.AddReviewRequest;
import com.toyshop.review.dto.ReviewPageResponse;
import com.toyshop.review.entity.ProductReview;
import com.toyshop.review.mapper.ProductReviewMapper;
import com.toyshop.review.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewMapper productReviewMapper;
    private final ProductMapper productMapper;

    public ReviewServiceImpl(ProductReviewMapper productReviewMapper, ProductMapper productMapper) {
        this.productReviewMapper = productReviewMapper;
        this.productMapper = productMapper;
    }

    @Override
    public Long add(Long userId, AddReviewRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在");
        }

        ProductReview review = new ProductReview();
        review.setUserId(userId);
        review.setProductId(request.getProductId());
        review.setOrderId(request.getOrderId());
        review.setScore(request.getScore());
        review.setContent(request.getContent());
        review.setPictures(request.getPictures());
        review.setIsAnonymous(request.getIsAnonymous() == null ? 0 : request.getIsAnonymous());
        review.setStatus(1);
        review.setCreatedAt(LocalDateTime.now());
        productReviewMapper.insert(review);
        return review.getId();
    }

    @Override
    public List<ProductReview> listByProduct(Long productId) {
        QueryWrapper<ProductReview> qw = new QueryWrapper<ProductReview>()
                .eq("product_id", productId)
                .eq("status", 1)
                .orderByDesc("created_at");
        return productReviewMapper.selectList(qw);
    }

    @Override
    public ReviewPageResponse adminList(Integer pageNum, Integer pageSize, Long productId, Integer status) {
        long current = (pageNum == null || pageNum < 1) ? 1L : pageNum;
        long size = (pageSize == null || pageSize < 1) ? 10L : pageSize;
        QueryWrapper<ProductReview> qw = new QueryWrapper<ProductReview>()
                .orderByDesc("created_at");
        if (productId != null) {
            qw.eq("product_id", productId);
        }
        if (status != null) {
            qw.eq("status", status);
        }
        Page<ProductReview> page = productReviewMapper.selectPage(new Page<>(current, size), qw);
        return new ReviewPageResponse(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public void updateStatus(Long reviewId, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "status 只能是 0 或 1");
        }
        ProductReview review = productReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "评价不存在");
        }
        review.setStatus(status);
        productReviewMapper.updateById(review);
    }
}
