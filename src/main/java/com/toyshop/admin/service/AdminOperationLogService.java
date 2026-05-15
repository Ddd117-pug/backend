package com.toyshop.admin.service;

import com.toyshop.admin.dto.AdminOperationLogPageResponse;
import com.toyshop.admin.log.AdminOperationLog;

public interface AdminOperationLogService {
    AdminOperationLogPageResponse page(String keyword, String module, String level, String startDate, String endDate, Integer pageNum, Integer pageSize);
    void create(AdminOperationLog log);
}
