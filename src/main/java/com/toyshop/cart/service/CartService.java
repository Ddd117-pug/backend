package com.toyshop.cart.service;

import com.toyshop.cart.dto.AddCartRequest;
import com.toyshop.cart.dto.CartItemVO;

import java.util.List;

public interface CartService {
    Long add(Long userId, AddCartRequest request);

    List<CartItemVO> list(Long userId);

    void update(Long userId, Long id, Integer num);

    void delete(Long userId, Long id);
}
