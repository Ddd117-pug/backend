package com.toyshop.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String phone;

    private String email;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 潮玩积分
     */
    private Integer points;

    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 角色：0-普通用户，1-管理员（示例）
     */
    private Integer role;

    /**
     * 逻辑删除：0-未删除，1-已注销
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    @TableField(value = "created_at")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;
}
