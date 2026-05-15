package com.toyshop.admin.dto;

import com.toyshop.admin.log.AdminOperationLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOperationLogPageResponse {
    private long total;
    private List<AdminOperationLog> records;
}
