package com.toyshop.home.service;

import com.toyshop.home.entity.HomeOperationConfig;

import java.util.List;

public interface HomeOperationConfigService {
    List<HomeOperationConfig> listAll();

    List<HomeOperationConfig> listActive();

    HomeOperationConfig getById(Long id);

    Long create(HomeOperationConfig config);

    void update(Long id, HomeOperationConfig config);

    void delete(Long id);
}
