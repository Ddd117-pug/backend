package com.toyshop.admin.dto;

import lombok.Data;

@Data
public class AdminOperationLogQuery {
    private String keyword;
    private String module;
    private String level;
    private String startDate;
    private String endDate;
    private Integer pageNum;
    private Integer pageSize;
}
