package com.toyshop.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.toyshop.aftersale.entity.AfterSaleOrder;
import com.toyshop.aftersale.mapper.AfterSaleOrderMapper;
import com.toyshop.aftersale.service.AfterSaleService;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.order.entity.ToyOrder;
import com.toyshop.order.mapper.ToyOrderMapper;
import com.toyshop.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AfterSaleServiceImpl implements AfterSaleService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_APPROVED = 1;
    private static final int STATUS_REJECTED = 2;
    private static final int STATUS_REFUNDED = 3;

    private final AfterSaleOrderMapper afterSaleOrderMapper;
    private final ToyOrderMapper toyOrderMapper;
    private final OrderService orderService;

    public AfterSaleServiceImpl(AfterSaleOrderMapper afterSaleOrderMapper,
                                ToyOrderMapper toyOrderMapper,
                                OrderService orderService) {
        this.afterSaleOrderMapper = afterSaleOrderMapper;
        this.toyOrderMapper = toyOrderMapper;
        this.orderService = orderService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(Long userId, Long orderId, String reason) {
        ToyOrder order = toyOrderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "订单不存在");
        }
        if (order.getStatus() == null || (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "当前订单状态不可申请售后");
        }
        AfterSaleOrder existing = afterSaleOrderMapper.selectOne(new QueryWrapper<AfterSaleOrder>()
                .eq("order_id", orderId)
                .orderByDesc("id")
                .last("limit 1"));
        if (existing != null && existing.getStatus() != null && existing.getStatus() != STATUS_REJECTED) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "该订单已有售后申请，请勿重复提交");
        }
        AfterSaleOrder record = new AfterSaleOrder();
        record.setOrderId(orderId);
        record.setUserId(userId);
        record.setOrderNo(order.getOrderNo());
        record.setReason(StringUtils.hasText(reason) ? reason.trim() : "其他");
        record.setStatus(STATUS_PENDING);
        record.setAppliedAt(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        afterSaleOrderMapper.insert(record);
    }

    @Override
    public List<AfterSaleOrder> userList(Long userId) {
        return afterSaleOrderMapper.selectList(new QueryWrapper<AfterSaleOrder>()
                .eq("user_id", userId)
                .orderByDesc("created_at"));
    }

    @Override
    public List<AfterSaleOrder> adminList() {
        return afterSaleOrderMapper.selectList(new QueryWrapper<AfterSaleOrder>()
                .orderByDesc("created_at"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long afterSaleId, String reply) {
        AfterSaleOrder record = mustGet(afterSaleId);
        if (record.getStatus() == null || record.getStatus() != STATUS_PENDING) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "当前售后状态不可审核通过");
        }
        record.setStatus(STATUS_APPROVED);
        record.setReply(StringUtils.hasText(reply) ? reply.trim() : "审核通过，同意退款");
        record.setAuditedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        afterSaleOrderMapper.updateById(record);

        orderService.refund(record.getUserId(), record.getOrderId());

        record.setStatus(STATUS_REFUNDED);
        record.setRefundedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        afterSaleOrderMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long afterSaleId, String reply) {
        AfterSaleOrder record = mustGet(afterSaleId);
        if (record.getStatus() == null || record.getStatus() != STATUS_PENDING) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "当前售后状态不可驳回");
        }
        record.setStatus(STATUS_REJECTED);
        record.setReply(StringUtils.hasText(reply) ? reply.trim() : "申请未通过，请联系管理员了解详情");
        record.setAuditedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        afterSaleOrderMapper.updateById(record);
    }

    private AfterSaleOrder mustGet(Long afterSaleId) {
        AfterSaleOrder record = afterSaleOrderMapper.selectById(afterSaleId);
        if (record == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "售后申请不存在");
        }
        return record;
    }
}
