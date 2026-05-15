package com.toyshop.home.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.home.entity.HomeOperationConfig;
import com.toyshop.home.mapper.HomeOperationConfigMapper;
import com.toyshop.home.service.HomeOperationConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HomeOperationConfigServiceImpl implements HomeOperationConfigService {
    private final HomeOperationConfigMapper mapper;

    public HomeOperationConfigServiceImpl(HomeOperationConfigMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<HomeOperationConfig> listAll() {
        return mapper.selectList(new LambdaQueryWrapper<HomeOperationConfig>().orderByAsc(HomeOperationConfig::getSortOrder).orderByDesc(HomeOperationConfig::getId));
    }

    @Override
    public List<HomeOperationConfig> listActive() {
        return mapper.selectList(new LambdaQueryWrapper<HomeOperationConfig>()
                .eq(HomeOperationConfig::getStatus, 1)
                .orderByAsc(HomeOperationConfig::getSortOrder)
                .orderByDesc(HomeOperationConfig::getId));
    }

    @Override
    public HomeOperationConfig getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public Long create(HomeOperationConfig config) {
        validate(config);
        config.setId(null);
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());
        if (config.getStatus() == null) config.setStatus(1);
        if (config.getSortOrder() == null) config.setSortOrder(0);
        mapper.insert(config);
        return config.getId();
    }

    @Override
    public void update(Long id, HomeOperationConfig config) {
        validate(config);
        HomeOperationConfig db = mapper.selectById(id);
        if (db == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "配置不存在");
        db.setType(config.getType());
        db.setTitle(config.getTitle());
        db.setSubtitle(config.getSubtitle());
        db.setImageUrl(config.getImageUrl());
        db.setLinkUrl(config.getLinkUrl());
        db.setTargetType(config.getTargetType());
        db.setTargetValue(config.getTargetValue());
        db.setSortOrder(config.getSortOrder() == null ? 0 : config.getSortOrder());
        db.setStatus(config.getStatus() == null ? 1 : config.getStatus());
        db.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(db);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    private void validate(HomeOperationConfig config) {
        if (config == null) throw new BusinessException(ResultCode.VALIDATION_ERROR, "配置不能为空");
        if (!StringUtils.hasText(config.getType())) throw new BusinessException(ResultCode.VALIDATION_ERROR, "类型不能为空");
        if (!StringUtils.hasText(config.getTitle())) throw new BusinessException(ResultCode.VALIDATION_ERROR, "标题不能为空");
    }
}
