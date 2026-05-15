package com.toyshop.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 放到 SecurityContext 的最小用户信息。
 */
@Data
@AllArgsConstructor
public class JwtUser {
    private Long userId;
    private String username;
    private Integer role;
}

