package com.toyshop.favorite.service;

import com.toyshop.favorite.dto.FavoriteProductResponse;

import java.util.List;

public interface UserFavoriteService {
    void add(Long userId, Long productId);

    void remove(Long userId, Long productId);

    List<FavoriteProductResponse> list(Long userId);
}
