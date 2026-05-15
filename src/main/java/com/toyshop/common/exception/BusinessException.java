package com.toyshop.common.exception;

import com.toyshop.common.response.ResultCode;

/**
 * 业务异常：由业务层抛出，统一返回给前端。
 */
public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}

