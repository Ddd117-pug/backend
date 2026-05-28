package com.toyshop.consultation.service.impl;

import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.consultation.dto.*;
import com.toyshop.consultation.entity.ToyConsultation;
import com.toyshop.consultation.entity.ToyConsultationMessage;
import com.toyshop.consultation.mapper.ConsultationMapper;
import com.toyshop.consultation.mapper.ConsultationMessageMapper;
import com.toyshop.consultation.service.ConsultationService;
import com.toyshop.admin.service.AdminService;
import com.toyshop.product.entity.Product;
import com.toyshop.product.service.ProductService;
import com.toyshop.user.entity.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationMapper consultationMapper;
    private final ConsultationMessageMapper messageMapper;
    private final ProductService productService;
    private final AdminService adminService;

    public ConsultationServiceImpl(ConsultationMapper consultationMapper,
                                   ConsultationMessageMapper messageMapper,
                                   ProductService productService,
                                   AdminService adminService) {
        this.consultationMapper = consultationMapper;
        this.messageMapper = messageMapper;
        this.productService = productService;
        this.adminService = adminService;
    }

    @Override
    public Long createConsultation(Long userId, CreateConsultationRequest request) {
        validateContent(request == null ? null : request.getContent());
        if (request == null || request.getProductId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "请选择咨询商品");
        }
        Product product = productService.detail(request.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "商品不存在");
        }
        ToyConsultation consultation = new ToyConsultation()
                .setUserId(userId)
                .setProductId(product.getId())
                .setSellerId(product.getBrandId())
                .setStatus(0)
                .setLastMessage(trimContent(request.getContent()))
                .setLastSenderType("user")
                .setUnreadUserCount(0)
                .setUnreadAdminCount(1)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        consultationMapper.insert(consultation);

        ToyConsultationMessage message = new ToyConsultationMessage()
                .setConsultationId(consultation.getId())
                .setSenderType("user")
                .setSenderId(userId)
                .setContent(trimContent(request.getContent()))
                .setMessageType("text")
                .setIsRead(1)
                .setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        return consultation.getId();
    }

    @Override
    public ConsultationPageResponse myConsultationPage(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        int pn = normalizePageNum(pageNum);
        int ps = normalizePageSize(pageSize);
        int offset = (pn - 1) * ps;
        List<ToyConsultation> consultations = consultationMapper.selectUserPage(userId, status, offset, ps);
        long total = consultationMapper.countUserPage(userId, status);
        return new ConsultationPageResponse(pn, ps, total, consultations.stream().map(this::toListItem).collect(Collectors.toList()));
    }

    @Override
    public ConsultationDetailResponse myConsultationDetail(Long userId, Long consultationId) {
        ToyConsultation consultation = mustOwnConsultation(userId, consultationId);
        List<ConsultationMessageResponse> messages = loadMessages(consultationId);
        markUserRead(userId, consultationId);
        return buildDetail(consultation, messages);
    }

    @Override
    public void sendMessage(Long userId, Long consultationId, String content) {
        validateContent(content);
        ToyConsultation consultation = mustOwnConsultation(userId, consultationId);
        if (Objects.equals(consultation.getStatus(), 2)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "咨询已关闭");
        }
        String text = trimContent(content);
        ToyConsultationMessage message = new ToyConsultationMessage()
                .setConsultationId(consultationId)
                .setSenderType("user")
                .setSenderId(userId)
                .setContent(text)
                .setMessageType("text")
                .setIsRead(1)
                .setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        consultationMapper.updateLastMessage(consultationId, text, "user", 0, 1, LocalDateTime.now(), 1);
    }

    @Override
    public void closeConsultation(Long userId, Long consultationId) {
        ToyConsultation consultation = mustOwnConsultation(userId, consultationId);
        consultationMapper.updateStatus(consultationId, 2, LocalDateTime.now(), LocalDateTime.now());
    }

    @Override
    public ConsultationPageResponse adminConsultationPage(String keyword, Long userId, Long productId, Integer status, Integer pageNum, Integer pageSize) {
        int pn = normalizePageNum(pageNum);
        int ps = normalizePageSize(pageSize);
        int offset = (pn - 1) * ps;
        List<ToyConsultation> consultations = consultationMapper.selectAdminPage(keyword, userId, productId, status, offset, ps);
        long total = consultationMapper.countAdminPage(keyword, userId, productId, status);
        return new ConsultationPageResponse(pn, ps, total, consultations.stream().map(this::toListItem).collect(Collectors.toList()));
    }

    @Override
    public ConsultationDetailResponse adminConsultationDetail(Long consultationId) {
        ToyConsultation consultation = mustConsultation(consultationId);
        List<ConsultationMessageResponse> messages = loadMessages(consultationId);
        return buildDetail(consultation, messages);
    }

    @Override
    public void adminReply(Long adminUserId, Long consultationId, ReplyConsultationRequest request) {
        validateContent(request == null ? null : request.getContent());
        ToyConsultation consultation = mustConsultation(consultationId);
        if (Objects.equals(consultation.getStatus(), 2)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "咨询已关闭");
        }
        String text = trimContent(request.getContent());
        ToyConsultationMessage message = new ToyConsultationMessage()
                .setConsultationId(consultationId)
                .setSenderType("admin")
                .setSenderId(adminUserId)
                .setContent(text)
                .setMessageType("text")
                .setIsRead(1)
                .setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        consultationMapper.updateLastMessage(consultationId, text, "admin", 1, 0, LocalDateTime.now(), 1);
    }

    @Override
    public void adminClose(Long consultationId) {
        mustConsultation(consultationId);
        consultationMapper.updateStatus(consultationId, 2, LocalDateTime.now(), LocalDateTime.now());
    }

    @Override
    public void adminMarkRead(Long consultationId) {
        consultationMapper.markAdminRead(consultationId);
    }

    @Override
    public void markUserRead(Long userId, Long consultationId) {
        ToyConsultation consultation = mustOwnConsultation(userId, consultationId);
        consultationMapper.markUserRead(consultation.getId());
    }

    private ToyConsultation mustOwnConsultation(Long userId, Long consultationId) {
        ToyConsultation consultation = mustConsultation(consultationId);
        if (!Objects.equals(consultation.getUserId(), userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该咨询");
        }
        return consultation;
    }

    private ToyConsultation mustConsultation(Long consultationId) {
        ToyConsultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "咨询记录不存在");
        }
        return consultation;
    }

    private List<ConsultationMessageResponse> loadMessages(Long consultationId) {
        List<ToyConsultationMessage> messages = messageMapper.selectByConsultationId(consultationId);
        if (messages == null) {
            return Collections.emptyList();
        }
        List<ConsultationMessageResponse> result = new ArrayList<>();
        for (ToyConsultationMessage message : messages) {
            result.add(new ConsultationMessageResponse(
                    message.getId(),
                    message.getConsultationId(),
                    message.getSenderType(),
                    message.getSenderId(),
                    message.getContent(),
                    message.getMessageType(),
                    message.getIsRead(),
                    message.getCreatedAt()
            ));
        }
        return result;
    }

    private ConsultationDetailResponse buildDetail(ToyConsultation consultation, List<ConsultationMessageResponse> messages) {
        Product product = productService.detail(consultation.getProductId());
        SysUser user = adminService.userDetail(consultation.getUserId());
        return new ConsultationDetailResponse(
                consultation.getId(),
                consultation.getUserId(),
                consultation.getProductId(),
                consultation.getSellerId(),
                consultation.getStatus(),
                consultation.getLastMessage(),
                consultation.getLastSenderType(),
                consultation.getUnreadUserCount(),
                consultation.getUnreadAdminCount(),
                product,
                user,
                messages
        );
    }

    private ConsultationListItemResponse toListItem(ToyConsultation consultation) {
        Product product = productService.detail(consultation.getProductId());
        SysUser user = adminService.userDetail(consultation.getUserId());
        return new ConsultationListItemResponse(
                consultation.getId(),
                consultation.getUserId(),
                user == null ? null : user.getUsername(),
                consultation.getProductId(),
                product == null ? null : product.getName(),
                product == null ? null : product.getCoverUrl(),
                consultation.getSellerId(),
                consultation.getStatus(),
                consultation.getLastMessage(),
                consultation.getLastSenderType(),
                consultation.getUnreadUserCount(),
                consultation.getUnreadAdminCount(),
                consultation.getCreatedAt(),
                consultation.getUpdatedAt()
        );
    }

    private void validateContent(String content) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "请输入咨询内容");
        }
    }

    private String trimContent(String content) {
        return content == null ? null : content.trim();
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
