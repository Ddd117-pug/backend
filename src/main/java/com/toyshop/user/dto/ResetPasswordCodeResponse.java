package com.toyshop.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordCodeResponse {
    private Long expireSeconds;
    private Long cooldownSeconds;
    private String demoCode;
}
