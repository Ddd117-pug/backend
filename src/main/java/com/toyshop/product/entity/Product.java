package com.toyshop.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("category_id")
    private Long categoryId;

    @TableField("brand_id")
    private Long brandId;

    private String name;

    @TableField("sub_title")
    private String subTitle;

    private String description;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("banner_urls")
    private String bannerUrls;

    @TableField("detail_image_urls")
    private String detailImageUrls;

    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    private Integer stock;

    @TableField("sale_count")
    private Integer saleCount;

    private String material;

    private String size;

    @TableField("style_desc")
    private String styleDesc;

    private Integer status;

    @TableField("is_hot")
    private Integer isHot;

    @TableField("is_new")
    private Integer isNew;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
