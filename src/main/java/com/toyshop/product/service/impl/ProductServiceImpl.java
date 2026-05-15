package com.toyshop.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.product.dto.PageResponse;
import com.toyshop.product.entity.Category;
import com.toyshop.product.entity.Product;
import com.toyshop.product.mapper.CategoryMapper;
import com.toyshop.product.mapper.ProductMapper;
import com.toyshop.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public ProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> categoryList() {
        return categoryMapper.selectAllEnabled();
    }

    @Override
    public PageResponse<Product> pageList(Long categoryId, Long brandId, Integer pageNum, Integer pageSize) {
        long current = pageNum == null || pageNum < 1 ? 1L : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10L : pageSize;
        IPage<Product> page = productMapper.selectPageList(new Page<>(current, size), categoryId, brandId);
        return new PageResponse<>(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public List<Product> hotList() {
        return productMapper.selectHotList(8);
    }

    @Override
    public List<Product> newList() {
        return productMapper.selectNewList(8);
    }

    @Override
    public Product detail(Long id) {
        Product product = productMapper.selectDetailById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在");
        }
        return product;
    }
}
