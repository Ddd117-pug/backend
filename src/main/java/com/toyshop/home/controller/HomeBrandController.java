package com.toyshop.home.controller;

import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import com.toyshop.product.entity.Brand;
import com.toyshop.product.mapper.BrandMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeBrandController {

    private final BrandMapper brandMapper;

    public HomeBrandController(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    @GetMapping("/brands")
    public ApiResponse<List<Brand>> brands() {
        return ApiResponse.success(brandMapper.selectAllWithProductCount());
    }

    @GetMapping("/brand/{id}")
    public ApiResponse<Brand> brandDetail(@PathVariable Long id) {
        Brand brand = brandMapper.selectByIdWithProductCount(id);
        if (brand == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "品牌不存在");
        }
        return ApiResponse.success(brand);
    }
}
