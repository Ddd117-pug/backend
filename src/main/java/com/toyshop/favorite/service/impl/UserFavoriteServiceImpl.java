package com.toyshop.favorite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.favorite.dto.FavoriteProductResponse;
import com.toyshop.favorite.entity.UserFavorite;
import com.toyshop.favorite.mapper.UserFavoriteMapper;
import com.toyshop.favorite.service.UserFavoriteService;
import com.toyshop.product.entity.Product;
import com.toyshop.product.mapper.ProductMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;
    private final ProductMapper productMapper;

    public UserFavoriteServiceImpl(UserFavoriteMapper userFavoriteMapper, ProductMapper productMapper) {
        this.userFavoriteMapper = userFavoriteMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在或已下架");
        }
        UserFavorite exists = userFavoriteMapper.selectOne(new QueryWrapper<UserFavorite>()
                .eq("user_id", userId)
                .eq("product_id", productId)
                .last("limit 1"));
        if (exists != null) {
            return;
        }
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        userFavoriteMapper.insert(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long userId, Long productId) {
        userFavoriteMapper.delete(new QueryWrapper<UserFavorite>()
                .eq("user_id", userId)
                .eq("product_id", productId));
    }

    @Override
    public List<FavoriteProductResponse> list(Long userId) {
        List<UserFavorite> favorites = userFavoriteMapper.selectList(new QueryWrapper<UserFavorite>()
                .eq("user_id", userId)
                .orderByDesc("created_at").orderByDesc("id"));
        List<FavoriteProductResponse> products = new ArrayList<>();
        for (UserFavorite favorite : favorites) {
            Product product = productMapper.selectById(favorite.getProductId());
            if (product != null && product.getStatus() != null && product.getStatus() == 1) {
                FavoriteProductResponse response = new FavoriteProductResponse();
                BeanUtils.copyProperties(product, response);
                response.setFavoriteTime(favorite.getCreatedAt());
                products.add(response);
            }
        }
        return products;
    }
}
