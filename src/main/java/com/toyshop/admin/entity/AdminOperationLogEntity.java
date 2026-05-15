package com.toyshop.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_operation_log")
public class AdminOperationLogEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String module;

    private String action;

    private String content;

    private String target;

    private String level;

    private Integer success;

    private String meta;

    private String operator;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
