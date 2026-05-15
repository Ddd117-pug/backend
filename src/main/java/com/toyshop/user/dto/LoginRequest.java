package com.toyshop.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名或手机号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
