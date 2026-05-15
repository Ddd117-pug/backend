package com.toyshop.review.dto;

import com.toyshop.review.entity.ProductReview;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReviewPageResponse {
    private long pageNum;
    private long pageSize;
    private long total;
    private List<ProductReview> records;
}

