package com.toyshop.user.dto;

import lombok.Data;

/**
 * 个人信息修改（不含密码）
 */
@Data
public class UpdateProfileRequest {
    private String phone;
    private String email;
    private String avatarUrl;
    private Integer gender;
}

