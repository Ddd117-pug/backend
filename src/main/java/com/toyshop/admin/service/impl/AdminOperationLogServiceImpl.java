package com.toyshop.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.toyshop.admin.dto.AdminOperationLogPageResponse;
import com.toyshop.admin.entity.AdminOperationLogEntity;
import com.toyshop.admin.log.AdminOperationLog;
import com.toyshop.admin.mapper.AdminOperationLogMapper;
import com.toyshop.admin.service.AdminOperationLogService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class AdminOperationLogServiceImpl implements AdminOperationLogService {
    private final AdminOperationLogMapper mapper;

    public AdminOperationLogServiceImpl(AdminOperationLogMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public AdminOperationLogPageResponse page(String keyword, String module, String level, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 20L : pageSize;
        QueryWrapper<AdminOperationLogEntity> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("content", keyword).or().like("target", keyword).or().like("action", keyword).or().like("module", keyword));
        }
        if (StringUtils.hasText(module)) qw.eq("module", module);
        if (StringUtils.hasText(level)) qw.eq("level", level);
        if (StringUtils.hasText(startDate)) qw.ge("created_at", parseStart(startDate));
        if (StringUtils.hasText(endDate)) qw.le("created_at", parseEnd(endDate));
        qw.orderByDesc("created_at");
        Page<AdminOperationLogEntity> page = mapper.selectPage(new Page<>(current, size), qw);
        return new AdminOperationLogPageResponse(page.getTotal(), page.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
    }

    @Override
    public void create(AdminOperationLog log) {
        if (log == null) throw new BusinessException(ResultCode.VALIDATION_ERROR, "日志不能为空");
        AdminOperationLogEntity entity = new AdminOperationLogEntity();
        entity.setModule(log.getModule());
        entity.setAction(log.getAction());
        entity.setContent(log.getContent());
        entity.setTarget(log.getTarget());
        entity.setLevel(StringUtils.hasText(log.getLevel()) ? log.getLevel() : "info");
        entity.setSuccess(log.getSuccess() == null ? 1 : (log.getSuccess() ? 1 : 0));
        entity.setMeta(log.getMeta());
        entity.setOperator(StringUtils.hasText(log.getOperator()) ? log.getOperator() : "admin");
        entity.setCreatedAt(log.getTime() == null ? LocalDateTime.now() : log.getTime());
        mapper.insert(entity);
    }

    private AdminOperationLog toVo(AdminOperationLogEntity entity) {
        AdminOperationLog vo = new AdminOperationLog();
        vo.setId(entity.getId());
        vo.setModule(entity.getModule());
        vo.setAction(entity.getAction());
        vo.setContent(entity.getContent());
        vo.setTarget(entity.getTarget());
        vo.setLevel(entity.getLevel());
        vo.setSuccess(entity.getSuccess() != null && entity.getSuccess() == 1);
        vo.setMeta(entity.getMeta());
        vo.setOperator(entity.getOperator());
        vo.setTime(entity.getCreatedAt());
        return vo;
    }

    private LocalDateTime parseStart(String date) {
        return LocalDateTime.parse(date + "T00:00:00");
    }

    private LocalDateTime parseEnd(String date) {
        return LocalDateTime.parse(date + "T23:59:59");
    }
}
