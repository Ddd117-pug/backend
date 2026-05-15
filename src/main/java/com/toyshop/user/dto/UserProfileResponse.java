package com.toyshop.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private BigDecimal balance;
    private Integer points;
    private String avatarUrl;
    private Integer gender;
    private Integer role;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
