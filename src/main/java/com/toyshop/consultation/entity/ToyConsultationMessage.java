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
@TableName("toy_consultation_message")
public class ToyConsultationMessage {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("consultation_id")
    private Long consultationId;

    @TableField("sender_type")
    private String senderType;

    @TableField("sender_id")
    private Long senderId;

    private String content;

    @TableField("message_type")
    private String messageType;

    @TableField("is_read")
    private Integer isRead;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
