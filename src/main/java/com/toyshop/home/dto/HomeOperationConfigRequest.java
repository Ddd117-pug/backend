package com.toyshop.home.dto;

import lombok.Data;

@Data
public class HomeOperationConfigRequest {
    private String type;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String linkUrl;
    private String targetType;
    private String targetValue;
    private Integer sortOrder;
    private Integer status;
}
