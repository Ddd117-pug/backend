package com.toyshop.favorite.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteProductResponse {
    private Long id;
    private Long categoryId;
    private String name;
    private String subTitle;
    private String description;
    private String coverUrl;
    private String bannerUrls;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer saleCount;
    private String material;
    private String size;
    private Integer isBlindBox;
    private String blindBoxInfo;
    private String styleDesc;
    private String blindBoxMode;
    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime favoriteTime;
}
