package com.toyshop.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.toyshop.product.entity.Brand;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BrandMapper extends BaseMapper<Brand> {
    List<Brand> selectAllEnabled();
    List<Brand> selectAllWithProductCount();
    List<Brand> selectAllWithProductCountAndRecent();
    Brand selectByIdWithProductCount(@org.apache.ibatis.annotations.Param("id") Long id);
}
