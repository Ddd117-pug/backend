package com.toyshop.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product_review")
public class ProductReview {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("product_id")
    private Long productId;

    @TableField("order_id")
    private Long orderId;

    private Integer score;

    private String content;

    /**
     * 评价图片，多张以逗号分隔或 JSON
     */
    private String pictures;

    @TableField("is_anonymous")
    private Integer isAnonymous;

    /**
     * 状态：1-正常 0-屏蔽/删除
     */
    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}

