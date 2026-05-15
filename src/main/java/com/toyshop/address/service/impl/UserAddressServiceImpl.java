package com.toyshop.address.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.toyshop.address.dto.AddressSaveRequest;
import com.toyshop.address.entity.UserAddress;
import com.toyshop.address.mapper.UserAddressMapper;
import com.toyshop.address.service.UserAddressService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressMapper userAddressMapper;

    public UserAddressServiceImpl(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    @Override
    public List<UserAddress> list(Long userId) {
        return userAddressMapper.selectList(new QueryWrapper<UserAddress>()
                .eq("user_id", userId)
                .orderByDesc("is_default")
                .orderByDesc("updated_at")
                .orderByDesc("id"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Long userId, AddressSaveRequest request) {
        Integer isDefault = normalizeDefaultFlag(request.getIsDefault());
        if (isDefault == 1 || getDefaultAddress(userId) == null) {
            clearDefault(userId);
            isDefault = 1;
        }

        UserAddress address = new UserAddress();
        fillAddress(address, request);
        address.setUserId(userId);
        address.setIsDefault(isDefault);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        userAddressMapper.insert(address);
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, Long addressId, AddressSaveRequest request) {
        UserAddress address = mustOwnAddress(userId, addressId);
        Integer isDefault = normalizeDefaultFlag(request.getIsDefault());
        if (isDefault == 1) {
            clearDefault(userId);
            address.setIsDefault(1);
        } else if (address.getIsDefault() == null || address.getIsDefault() != 1) {
            address.setIsDefault(0);
        }
        fillAddress(address, request);
        address.setUpdatedAt(LocalDateTime.now());
        userAddressMapper.updateById(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long addressId) {
        UserAddress address = mustOwnAddress(userId, addressId);
        userAddressMapper.deleteById(addressId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            List<UserAddress> remaining = list(userId);
            if (!remaining.isEmpty()) {
                UserAddress next = remaining.get(0);
                next.setIsDefault(1);
                next.setUpdatedAt(LocalDateTime.now());
                userAddressMapper.updateById(next);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long addressId) {
        UserAddress address = mustOwnAddress(userId, addressId);
        clearDefault(userId);
        address.setIsDefault(1);
        address.setUpdatedAt(LocalDateTime.now());
        userAddressMapper.updateById(address);
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        return userAddressMapper.selectOne(new QueryWrapper<UserAddress>()
                .eq("user_id", userId)
                .eq("is_default", 1)
                .last("limit 1"));
    }

    private UserAddress mustOwnAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "收货地址不存在");
        }
        return address;
    }

    private void clearDefault(Long userId) {
        List<UserAddress> list = userAddressMapper.selectList(new QueryWrapper<UserAddress>().eq("user_id", userId));
        for (UserAddress item : list) {
            if (item.getIsDefault() != null && item.getIsDefault() == 1) {
                item.setIsDefault(0);
                item.setUpdatedAt(LocalDateTime.now());
                userAddressMapper.updateById(item);
            }
        }
    }

    private Integer normalizeDefaultFlag(Integer isDefault) {
        return isDefault != null && isDefault == 1 ? 1 : 0;
    }

    private void fillAddress(UserAddress address, AddressSaveRequest request) {
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(cleanRegion(request.getProvince()));
        address.setCity(cleanRegion(request.getCity()));
        address.setDistrict(cleanRegion(request.getDistrict()));
        address.setDetail(request.getDetail());
        address.setPostalCode(clean(request.getPostalCode()));
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String cleanRegion(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }
}
