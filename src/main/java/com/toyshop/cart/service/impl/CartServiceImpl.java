package com.toyshop.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.toyshop.cart.dto.AddCartRequest;
import com.toyshop.cart.dto.CartItemVO;
import com.toyshop.cart.entity.CartItem;
import com.toyshop.cart.mapper.CartItemMapper;
import com.toyshop.cart.service.CartService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.product.entity.Product;
import com.toyshop.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartItemMapper cartItemMapper, ProductMapper productMapper) {
        this.cartItemMapper = cartItemMapper;
        this.productMapper = productMapper;
    }

    @Override
    public Long add(Long userId, AddCartRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在或已下架");
        }

        String styleOption = request.getStyleOption();
        CartItem existed = cartItemMapper.selectOne(new QueryWrapper<CartItem>()
                .eq("user_id", userId)
                .eq("product_id", request.getProductId())
                .eq(styleOption != null, "style_option", styleOption)
                .isNull(styleOption == null, "style_option")
                .last("limit 1"));
        if (existed != null) {
            existed.setNum(existed.getNum() + request.getNum());
            cartItemMapper.updateById(existed);
            return existed.getId();
        }

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(request.getProductId());
        item.setPrice(product.getPrice());
        item.setStyleOption(styleOption);
        item.setNum(request.getNum());
        item.setCreateTime(LocalDateTime.now());
        cartItemMapper.insert(item);
        return item.getId();
    }

    @Override
    public List<CartItemVO> list(Long userId) {
        List<CartItem> list = cartItemMapper.selectList(new QueryWrapper<CartItem>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        List<CartItemVO> result = new ArrayList<>();
        for (CartItem item : list) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                continue;
            }
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            vo.setUserId(item.getUserId());
            vo.setProductId(item.getProductId());
            vo.setProductName(product.getName());
            vo.setCoverUrl(product.getCoverUrl());
            vo.setPrice(item.getPrice());
            vo.setStyleOption(item.getStyleOption());
            vo.setNum(item.getNum());
            vo.setMaterial(product.getMaterial());
            vo.setSize(product.getSize());
            BigDecimal price = item.getPrice() == null ? BigDecimal.ZERO : item.getPrice();
            int num = item.getNum() == null ? 0 : item.getNum();
            vo.setTotalPrice(price.multiply(BigDecimal.valueOf(num)));
            result.add(vo);
        }
        return result;
    }

    @Override
    public void update(Long userId, Long id, Integer num) {
        CartItem item = getUserCartItem(userId, id);
        item.setNum(num);
        cartItemMapper.updateById(item);
    }

    @Override
    public void delete(Long userId, Long id) {
        CartItem item = getUserCartItem(userId, id);
        cartItemMapper.deleteById(item.getId());
    }

    private CartItem getUserCartItem(Long userId, Long id) {
        CartItem item = cartItemMapper.selectById(id);
        if (item == null || !userId.equals(item.getUserId())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "购物车商品不存在");
        }
        return item;
    }
}
