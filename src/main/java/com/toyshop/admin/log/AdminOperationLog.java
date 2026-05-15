package com.toyshop.admin.log;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOperationLog {
    private Long id;
    private String module;
    private String action;
    private String content;
    private String target;
    private String level;
    private Boolean success;
    private String meta;
    private String operator;
    private LocalDateTime time;
}
