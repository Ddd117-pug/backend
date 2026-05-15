package com.toyshop.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回格式：{ code, message, data, timestamp }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode(ResultCode.SUCCESS.getCode());
        res.setMessage(ResultCode.SUCCESS.getMessage());
        res.setData(data);
        res.setTimestamp(System.currentTimeMillis());
        return res;
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode) {
        return fail(resultCode, resultCode.getMessage());
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode(resultCode.getCode());
        res.setMessage(message);
        res.setData(null);
        res.setTimestamp(System.currentTimeMillis());
        return res;
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode(code);
        res.setMessage(message);
        res.setData(null);
        res.setTimestamp(System.currentTimeMillis());
        return res;
    }
}

