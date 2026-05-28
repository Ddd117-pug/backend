package com.toyshop.consultation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConsultationPageResponse {
    private long pageNum;
    private long pageSize;
    private long total;
    private List<ConsultationListItemResponse> records;
}
