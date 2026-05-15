package com.toyshop.product.service;

import com.toyshop.product.dto.PageResponse;
import com.toyshop.product.entity.Category;
import com.toyshop.product.entity.Product;

import java.util.List;

public interface ProductService {
    List<Category> categoryList();

    PageResponse<Product> pageList(Long categoryId, Long brandId, Integer pageNum, Integer pageSize);

    List<Product> hotList();

    List<Product> newList();

    Product detail(Long id);
}
