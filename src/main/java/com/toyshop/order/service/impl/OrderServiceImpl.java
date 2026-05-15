package com.toyshop.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.toyshop.address.entity.UserAddress;
import com.toyshop.address.service.UserAddressService;
import com.toyshop.cart.entity.CartItem;
import com.toyshop.cart.mapper.CartItemMapper;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.order.dto.CreateOrderRequest;
import com.toyshop.order.dto.OrderDetailResponse;
import com.toyshop.order.dto.OrderPageResponse;
import com.toyshop.order.entity.ToyOrder;
import com.toyshop.order.entity.ToyOrderItem;
import com.toyshop.order.mapper.ToyOrderItemMapper;
import com.toyshop.order.mapper.ToyOrderMapper;
import com.toyshop.order.service.OrderService;
import com.toyshop.product.entity.Product;
import com.toyshop.product.mapper.ProductMapper;
import com.toyshop.user.entity.SysUser;
import com.toyshop.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final int STATUS_PENDING_PAY = 0;
    private static final int STATUS_PAID = 1;
    private static final int STATUS_SHIPPED = 2;
    private static final int STATUS_FINISHED = 3;
    private static final int STATUS_CANCELLED = 4;
    private static final int PAY_TYPE_BALANCE = 0;
    private static final int PAY_TYPE_WECHAT = 1;
    private static final int PAY_TYPE_ALIPAY = 2;

    private final ToyOrderMapper toyOrderMapper;
    private final ToyOrderItemMapper toyOrderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final SysUserMapper sysUserMapper;
    private final UserAddressService userAddressService;

    public OrderServiceImpl(ToyOrderMapper toyOrderMapper,
                            ToyOrderItemMapper toyOrderItemMapper,
                            CartItemMapper cartItemMapper,
                            ProductMapper productMapper,
                            SysUserMapper sysUserMapper,
                            UserAddressService userAddressService) {
        this.toyOrderMapper = toyOrderMapper;
        this.toyOrderItemMapper = toyOrderItemMapper;
        this.cartItemMapper = cartItemMapper;
        this.productMapper = productMapper;
        this.sysUserMapper = sysUserMapper;
        this.userAddressService = userAddressService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long userId, CreateOrderRequest request) {
        List<CartItem> cartItems = buildOrderSourceItems(userId, request);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "购物车为空，无法下单");
        }

        BigDecimal productAmount = BigDecimal.ZERO;
        BigDecimal freightAmount = BigDecimal.ZERO;
        ToyOrder order = buildBaseOrder(userId, request, freightAmount);
        toyOrderMapper.insert(order);

        for (CartItem item : cartItems) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "存在已下架商品，无法下单");
            }
            if (product.getStock() == null || product.getStock() < item.getNum()) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品库存不足: " + product.getName());
            }

            BigDecimal linePrice = item.getPrice() == null ? product.getPrice() : item.getPrice();
            BigDecimal lineAmount = linePrice.multiply(BigDecimal.valueOf(item.getNum()));
            productAmount = productAmount.add(lineAmount);

            ToyOrderItem orderItem = new ToyOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductPic(product.getCoverUrl());
            orderItem.setPrice(linePrice);
            orderItem.setQuantity(item.getNum());
            orderItem.setStyleOption(item.getStyleOption());
            orderItem.setAmount(lineAmount);
            orderItem.setCreatedAt(LocalDateTime.now());
            toyOrderItemMapper.insert(orderItem);
        }

        order.setProductAmount(productAmount);
        order.setTotalAmount(productAmount.add(freightAmount));
        order.setUpdatedAt(LocalDateTime.now());
        toyOrderMapper.updateById(order);

        if (request.getProductId() == null) {
            for (CartItem item : cartItems) {
                cartItemMapper.deleteById(item.getId());
            }
        }
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long userId, Long orderId, Integer payType) {
        ToyOrder order = mustOwnOrder(userId, orderId);
        if (order.getStatus() != STATUS_PENDING_PAY) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "当前订单状态不支持支付");
        }
        if (payType == null || (payType != PAY_TYPE_BALANCE && payType != PAY_TYPE_WECHAT && payType != PAY_TYPE_ALIPAY)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "支付方式不正确");
        }

        List<ToyOrderItem> items = toyOrderItemMapper.selectList(new QueryWrapper<ToyOrderItem>().eq("order_id", orderId));
        for (ToyOrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在，无法支付");
            }
            if (product.getStock() == null || product.getStock() < item.getQuantity()) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品库存不足: " + item.getProductName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            product.setSaleCount((product.getSaleCount() == null ? 0 : product.getSaleCount()) + item.getQuantity());
            product.setUpdatedAt(LocalDateTime.now());
            productMapper.updateById(product);
        }

        if (payType == PAY_TYPE_BALANCE) {
            SysUser user = mustActiveUser(userId);
            BigDecimal totalAmount = defaultAmount(order.getTotalAmount());
            if (defaultAmount(user.getBalance()).compareTo(totalAmount) < 0) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "余额不足，请先充值或选择其他支付方式");
            }
            user.setBalance(defaultAmount(user.getBalance()).subtract(totalAmount));
            user.setUpdatedAt(LocalDateTime.now());
            sysUserMapper.updateById(user);
        }

        order.setPayType(payType);
        order.setStatus(STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        toyOrderMapper.updateById(order);
    }

    @Override
    public OrderPageResponse list(Long userId, Integer pageNum, Integer pageSize) {
        long current = (pageNum == null || pageNum < 1) ? 1L : pageNum;
        long size = (pageSize == null || pageSize < 1) ? 10L : pageSize;
        QueryWrapper<ToyOrder> qw = new QueryWrapper<ToyOrder>()
                .eq("user_id", userId)
                .orderByDesc("created_at");
        Page<ToyOrder> page = toyOrderMapper.selectPage(new Page<>(current, size), qw);
        return new OrderPageResponse(current, size, page.getTotal(), page.getRecords());
    }

    @Override
    public OrderDetailResponse detail(Long userId, Long orderId) {
        ToyOrder order = mustOwnOrder(userId, orderId);
        List<ToyOrderItem> items = toyOrderItemMapper.selectList(new QueryWrapper<ToyOrderItem>().eq("order_id", orderId));
        return new OrderDetailResponse(order, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long orderId) {
        ToyOrder order = mustOwnOrder(userId, orderId);
        if (order.getStatus() != STATUS_PENDING_PAY) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "仅待支付订单可取消");
        }
        order.setStatus(STATUS_CANCELLED);
        order.setCloseTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        toyOrderMapper.updateById(order);
    }

    @Override
    public void ship(Long orderId) {
        ToyOrder order = toyOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "订单不存在");
        }
        if (order.getStatus() != STATUS_PAID) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "仅已支付订单可发货");
        }
        order.setStatus(STATUS_SHIPPED);
        order.setUpdatedAt(LocalDateTime.now());
        toyOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receive(Long userId, Long orderId) {
        ToyOrder order = mustOwnOrder(userId, orderId);
        if (order.getStatus() != STATUS_SHIPPED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "仅已发货订单可确认收货");
        }

        SysUser user = mustActiveUser(userId);
        int gainedPoints = defaultAmount(order.getTotalAmount()).intValue();
        user.setPoints(defaultPoints(user.getPoints()) + gainedPoints);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        order.setStatus(STATUS_FINISHED);
        order.setFinishTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        toyOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long userId, Long orderId) {
        ToyOrder order = mustOwnOrder(userId, orderId);
        refund(orderId);
        order.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long orderId) {
        ToyOrder order = toyOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "订单不存在");
        }
        if (order.getStatus() == null || (order.getStatus() != STATUS_PAID && order.getStatus() != STATUS_SHIPPED)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "仅已支付或已发货订单可退款");
        }

        rollbackStockAndSaleCount(orderId);

        SysUser user = mustActiveUser(order.getUserId());
        user.setBalance(defaultAmount(user.getBalance()).add(defaultAmount(order.getTotalAmount())));
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        order.setStatus(STATUS_CANCELLED);
        order.setCloseTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        toyOrderMapper.updateById(order);
    }

    private ToyOrder mustOwnOrder(Long userId, Long orderId) {
        ToyOrder order = toyOrderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "订单不存在");
        }
        return order;
    }

    private SysUser mustActiveUser(Long userId) {
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("id", userId)
                .eq("is_deleted", 0)
                .eq("status", 1)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在或登录已失效");
        }
        return user;
    }

    private void rollbackStockAndSaleCount(Long orderId) {
        for (ToyOrderItem item : toyOrderItemMapper.selectList(new QueryWrapper<ToyOrderItem>().eq("order_id", orderId))) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) continue;
            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            product.setStock((product.getStock() == null ? 0 : product.getStock()) + qty);
            product.setSaleCount(Math.max(0, (product.getSaleCount() == null ? 0 : product.getSaleCount()) - qty));
            product.setUpdatedAt(LocalDateTime.now());
            productMapper.updateById(product);
        }
    }

    private ToyOrder buildBaseOrder(Long userId, CreateOrderRequest request, BigDecimal freightAmount) {
        ResolvedAddress address = resolveAddress(userId, request);
        ToyOrder order = new ToyOrder();
        order.setUserId(userId);
        order.setOrderNo("TOY" + System.currentTimeMillis());
        order.setStatus(STATUS_PENDING_PAY);
        order.setPayType(request.getPayType() == null ? 1 : request.getPayType());
        order.setReceiverName(address.receiverName);
        order.setReceiverPhone(address.receiverPhone);
        order.setReceiverAddress(address.receiverAddress);
        order.setRemark(request.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setProductAmount(BigDecimal.ZERO);
        order.setFreightAmount(freightAmount);
        order.setTotalAmount(BigDecimal.ZERO);
        return order;
    }

    private List<CartItem> buildOrderSourceItems(Long userId, CreateOrderRequest request) {
        if (request.getProductId() == null) {
            List<CartItem> cartItems = cartItemMapper.selectList(new QueryWrapper<CartItem>().eq("user_id", userId));
            if (request.getCartIds() == null || request.getCartIds().isEmpty()) {
                return cartItems;
            }
            List<Long> selectedIds = request.getCartIds().stream()
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            List<CartItem> selectedItems = cartItems.stream()
                    .filter(item -> item.getId() != null && selectedIds.contains(item.getId()))
                    .collect(Collectors.toList());
            if (selectedItems.size() != selectedIds.size()) {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "存在无效的购物车商品选择");
            }
            return selectedItems;
        }

        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在或已下架");
        }
        int num = request.getNum() == null ? 1 : request.getNum();
        if (num < 1) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "购买数量必须大于0");
        }

        CartItem item = new CartItem();
        item.setProductId(product.getId());
        item.setPrice(product.getPrice());
        item.setNum(num);
        item.setStyleOption(normalizeStyleOption(product, request.getStyleOption()));
        return Collections.singletonList(item);
    }

    private String normalizeStyleOption(Product product, String styleOption) {
        if (product == null || product.getIsBlindBox() == null || product.getIsBlindBox() != 1) {
            return null;
        }
        if (!StringUtils.hasText(product.getStyleDesc())) {
            return null;
        }
        String[] options = product.getStyleDesc().split("\\r?\\n|；|;");
        if (!StringUtils.hasText(styleOption)) {
            for (String option : options) {
                if (StringUtils.hasText(option)) {
                    return option.trim();
                }
            }
            return null;
        }
        String normalized = styleOption.trim();
        for (String option : options) {
            if (normalized.equals(option.trim())) {
                return normalized;
            }
        }
        throw new BusinessException(ResultCode.VALIDATION_ERROR, "所选盲盒款式不存在");
    }

    private ResolvedAddress resolveAddress(Long userId, CreateOrderRequest request) {
        if (StringUtils.hasText(request.getReceiverName())
                && StringUtils.hasText(request.getReceiverPhone())
                && StringUtils.hasText(request.getReceiverAddress())) {
            return new ResolvedAddress(request.getReceiverName().trim(), request.getReceiverPhone().trim(), request.getReceiverAddress().trim());
        }
        UserAddress address = userAddressService.getDefaultAddress(userId);
        if (address == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "请先添加收货地址");
        }
        return new ResolvedAddress(address.getReceiverName(), address.getReceiverPhone(), formatAddress(address));
    }

    private String formatAddress(UserAddress item) {
        StringBuilder builder = new StringBuilder();
        appendAddress(builder, item.getProvince());
        appendAddress(builder, item.getCity());
        appendAddress(builder, item.getDistrict());
        appendAddress(builder, item.getDetail());
        return builder.toString().trim();
    }

    private void appendAddress(StringBuilder builder, String part) {
        if (!StringUtils.hasText(part)) return;
        if (builder.length() > 0) builder.append(' ');
        builder.append(part.trim());
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private Integer defaultPoints(Integer points) {
        return points == null ? 0 : points;
    }

    private static class ResolvedAddress {
        private final String receiverName;
        private final String receiverPhone;
        private final String receiverAddress;

        private ResolvedAddress(String receiverName, String receiverPhone, String receiverAddress) {
            this.receiverName = receiverName;
            this.receiverPhone = receiverPhone;
            this.receiverAddress = receiverAddress;
        }
    }
}
