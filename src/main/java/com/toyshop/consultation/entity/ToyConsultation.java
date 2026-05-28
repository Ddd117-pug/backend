package com.toyshop.consultation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("toy_consultation")
public class ToyConsultation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("product_id")
    private Long productId;

    @TableField("seller_id")
    private Long sellerId;

    /** 0-待回复 1-已回复 2-已关闭 3-已解决 */
    private Integer status;

    @TableField("last_message")
    private String lastMessage;

    @TableField("last_sender_type")
    private String lastSenderType;

    @TableField("unread_user_count")
    private Integer unreadUserCount;

    @TableField("unread_admin_count")
    private Integer unreadAdminCount;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("closed_at")
    private LocalDateTime closedAt;
}
