package com.toyshop.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.toyshop.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    IPage<Product> selectPageList(Page<Product> page, @Param("categoryId") Long categoryId, @Param("brandId") Long brandId);

    List<Product> selectHotList(@Param("limit") Integer limit);

    List<Product> selectNewList(@Param("limit") Integer limit);

    Product selectDetailById(@Param("id") Long id);
}
