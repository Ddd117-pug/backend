package com.toyshop.review.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AddReviewRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    private Long orderId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer score;

    private String content;

    /**
     * 图片列表（逗号分隔或 JSON）
     */
    private String pictures;

    /**
     * 是否匿名：1-是 0-否
     */
    private Integer isAnonymous;
}

