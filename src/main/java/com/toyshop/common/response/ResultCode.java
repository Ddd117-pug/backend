package com.toyshop.common.response;

/**
 * 统一业务码：code 为前端展示/判断用，message 为对外提示信息。
 */
public enum ResultCode {
    SUCCESS(0, "成功"),
    VALIDATION_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    BUSINESS_ERROR(1000, "业务异常"),
    SYSTEM_ERROR(5000, "系统异常");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

