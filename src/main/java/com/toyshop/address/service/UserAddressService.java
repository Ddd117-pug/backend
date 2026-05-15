package com.toyshop.address.service;

import com.toyshop.address.dto.AddressSaveRequest;
import com.toyshop.address.entity.UserAddress;

import java.util.List;

public interface UserAddressService {
    List<UserAddress> list(Long userId);

    Long add(Long userId, AddressSaveRequest request);

    void update(Long userId, Long addressId, AddressSaveRequest request);

    void delete(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);

    UserAddress getDefaultAddress(Long userId);
}
