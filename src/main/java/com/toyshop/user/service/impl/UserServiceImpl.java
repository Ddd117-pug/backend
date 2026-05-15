package com.toyshop.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.toyshop.cart.entity.CartItem;
import com.toyshop.cart.mapper.CartItemMapper;
import com.toyshop.common.exception.BusinessException;
import com.toyshop.common.response.ResultCode;
import com.toyshop.favorite.entity.UserFavorite;
import com.toyshop.favorite.mapper.UserFavoriteMapper;
import com.toyshop.security.JwtUtil;
import com.toyshop.user.controller.RegisterRequest;
import com.toyshop.user.dto.ChangePasswordRequest;
import com.toyshop.user.dto.ForgotPasswordRequest;
import com.toyshop.user.dto.LoginRequest;
import com.toyshop.user.dto.LoginResponse;
import com.toyshop.user.dto.ResetPasswordCodeResponse;
import com.toyshop.user.dto.ResetPasswordRequest;
import com.toyshop.user.dto.UpdateProfileRequest;
import com.toyshop.user.dto.UserProfileResponse;
import com.toyshop.user.entity.SysUser;
import com.toyshop.user.mapper.SysUserMapper;
import com.toyshop.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserServiceImpl implements UserService {

    private static final Map<String, ResetCodeSession> RESET_CODE_CACHE = new ConcurrentHashMap<>();

    private final SysUserMapper sysUserMapper;
    private final CartItemMapper cartItemMapper;
    private final UserFavoriteMapper userFavoriteMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;

    @Value("${toyshop.mail.from}")
    private String mailFrom;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${toyshop.reset-password.expire-seconds:300}")
    private long resetCodeExpireSeconds;

    @Value("${toyshop.reset-password.cooldown-seconds:60}")
    private long resetCodeCooldownSeconds;

    public UserServiceImpl(SysUserMapper sysUserMapper,
                           CartItemMapper cartItemMapper,
                           UserFavoriteMapper userFavoriteMapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           JavaMailSender mailSender) {
        this.sysUserMapper = sysUserMapper;
        this.cartItemMapper = cartItemMapper;
        this.userFavoriteMapper = userFavoriteMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
    }

    @Override
    public Long register(RegisterRequest request) {
        SysUser usernameExists = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("username", request.getUsername())
                .eq("is_deleted", 0)
                .last("limit 1"));
        if (usernameExists != null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "用户名已存在");

        SysUser phoneExists = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("phone", request.getPhone())
                .eq("is_deleted", 0)
                .last("limit 1"));
        if (phoneExists != null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "手机号已注册");

        SysUser emailExists = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("email", request.getEmail().trim())
                .eq("is_deleted", 0)
                .last("limit 1"));
        if (emailExists != null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "邮箱已注册");

        SysUser user = new SysUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone().trim());
        user.setEmail(request.getEmail().trim());
        user.setBalance(BigDecimal.ZERO);
        user.setPoints(0);
        user.setGender(0);
        user.setRole(0);
        user.setIsDeleted(0);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);
        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String loginKey = request.getUsername() == null ? "" : request.getUsername().trim();
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("is_deleted", 0)
                .and(w -> w.eq("username", loginKey).or().eq("phone", loginKey))
                .last("limit 1"));
        if (user == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "用户名/手机号或密码错误");
        if (user.getStatus() != null && user.getStatus() == 0) throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "用户名/手机号或密码错误");
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public ResetPasswordCodeResponse forgotPassword(ForgotPasswordRequest request) {
        SysUser user = getUserByAccount(request.getAccount());
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        if (user == null || !email.equalsIgnoreCase(defaultString(user.getEmail()))) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "账号与邮箱不匹配");
        }

        String cacheKey = buildResetKey(request.getAccount(), email);
        ResetCodeSession existed = RESET_CODE_CACHE.get(cacheKey);
        if (existed != null && existed.cooldownExpireAt.isAfter(LocalDateTime.now())) {
            long remainSeconds = Math.max(1, ChronoUnit.SECONDS.between(LocalDateTime.now(), existed.cooldownExpireAt));
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "请在 " + remainSeconds + " 秒后重新获取验证码");
        }

        String code = generateResetCode();
        LocalDateTime now = LocalDateTime.now();
        RESET_CODE_CACHE.put(cacheKey, new ResetCodeSession(
                code,
                now.plusSeconds(resetCodeExpireSeconds),
                now.plusSeconds(resetCodeCooldownSeconds),
                user.getId()
        ));
        boolean demoMode = isMailDemoMode();
        if (!demoMode) {
            sendResetCodeEmail(user.getUsername(), email, code);
        }
        return demoMode
                ? new ResetPasswordCodeResponse(resetCodeExpireSeconds, resetCodeCooldownSeconds, code)
                : new ResetPasswordCodeResponse(resetCodeExpireSeconds, resetCodeCooldownSeconds, null);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        SysUser user = getUserByAccount(request.getAccount());
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        if (user == null || !email.equalsIgnoreCase(defaultString(user.getEmail()))) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "账号与邮箱不匹配");
        }
        String cacheKey = buildResetKey(request.getAccount(), email);
        ResetCodeSession session = RESET_CODE_CACHE.get(cacheKey);
        if (session == null) throw new BusinessException(ResultCode.BUSINESS_ERROR, "请先获取验证码");
        if (session.expireAt.isBefore(LocalDateTime.now())) {
            RESET_CODE_CACHE.remove(cacheKey);
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "验证码已过期，请重新获取");
        }
        if (!session.userId.equals(user.getId()) || !session.code.equals(request.getCode().trim())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "验证码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
        RESET_CODE_CACHE.remove(cacheKey);
    }

    @Override
    public UserProfileResponse me(Long userId) {
        SysUser user = ensureActiveUserExists(userId);
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getPhone(), user.getEmail(), defaultBalance(user.getBalance()), defaultPoints(user.getPoints()), user.getAvatarUrl(), user.getGender(), user.getRole(), user.getStatus(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        SysUser user = ensureActiveUserExists(userId);
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getGender() != null) user.setGender(request.getGender());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = ensureActiveUserExists(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recharge(Long userId, BigDecimal amount) {
        SysUser user = ensureActiveUserExists(userId);
        user.setBalance(defaultBalance(user.getBalance()).add(amount));
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchangePoints(Long userId, Integer points) {
        if (points == null || points < 100) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "至少需要兑换 100 积分");
        }
        if (points % 100 != 0) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "积分兑换需为 100 的整数倍");
        }
        SysUser user = ensureActiveUserExists(userId);
        int currentPoints = defaultPoints(user.getPoints());
        if (currentPoints < points) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "积分不足，无法兑换");
        }
        BigDecimal exchangeAmount = BigDecimal.valueOf(points).divide(BigDecimal.valueOf(100));
        user.setPoints(currentPoints - points);
        user.setBalance(defaultBalance(user.getBalance()).add(exchangeAmount));
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    public void logout(Long userId) {
        ensureActiveUserExists(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAccount(Long userId) {
        SysUser user = ensureActiveUserExists(userId);
        user.setIsDeleted(1);
        user.setStatus(0);
        user.setUsername(user.getUsername() + "_deleted_" + user.getId());
        if (StringUtils.hasText(user.getPhone())) {
            user.setPhone(user.getPhone() + "_deleted_" + user.getId());
        }
        if (StringUtils.hasText(user.getEmail())) {
            user.setEmail(user.getEmail() + ".deleted." + user.getId());
        }
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        cartItemMapper.delete(new QueryWrapper<CartItem>().eq("user_id", userId));
        userFavoriteMapper.delete(new QueryWrapper<UserFavorite>().eq("user_id", userId));
    }

    private SysUser ensureActiveUserExists(Long userId) {
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("id", userId)
                .eq("is_deleted", 0)
                .last("limit 1"));
        if (user == null) throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在或登录已失效");
        return user;
    }

    private SysUser getUserByAccount(String account) {
        String loginKey = account == null ? "" : account.trim();
        return sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("is_deleted", 0)
                .and(w -> w.eq("username", loginKey).or().eq("phone", loginKey))
                .last("limit 1"));
    }

    private String buildResetKey(String account, String email) {
        return (account == null ? "" : account.trim()) + "#" + (email == null ? "" : email.trim().toLowerCase());
    }

    private String generateResetCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void sendResetCodeEmail(String username, String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("潮玩商城找回密码验证码");
            message.setText("尊敬的 " + username + "，您好：\n\n您正在申请重置潮玩商城账号密码。\n本次验证码为：" + code + "\n验证码 " + resetCodeExpireSeconds + " 秒内有效，请勿泄露给他人。\n\n如非本人操作，请忽略此邮件。\n\n潮玩商城系统");
            mailSender.send(message);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "验证码发送失败，请稍后重试");
        }
    }

    private boolean isMailDemoMode() {
        return !StringUtils.hasText(mailUsername)
                || !StringUtils.hasText(mailPassword)
                || mailUsername.contains("your_email")
                || mailPassword.contains("your_mail_auth_code")
                || !StringUtils.hasText(mailFrom)
                || mailFrom.contains("your_email");
    }

    private String defaultString(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal defaultBalance(BigDecimal balance) {
        return balance == null ? BigDecimal.ZERO : balance;
    }

    private Integer defaultPoints(Integer points) {
        return points == null ? 0 : points;
    }

    private static class ResetCodeSession {
        private final String code;
        private final LocalDateTime expireAt;
        private final LocalDateTime cooldownExpireAt;
        private final Long userId;

        private ResetCodeSession(String code, LocalDateTime expireAt, LocalDateTime cooldownExpireAt, Long userId) {
            this.code = code;
            this.expireAt = expireAt;
            this.cooldownExpireAt = cooldownExpireAt;
            this.userId = userId;
        }
    }
}
