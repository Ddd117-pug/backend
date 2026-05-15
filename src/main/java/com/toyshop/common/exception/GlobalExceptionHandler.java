package com.toyshop.common.exception;

import com.toyshop.common.response.ApiResponse;
import com.toyshop.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException ex) {
        ResultCode rc = ex.getResultCode();
        return ApiResponse.fail(rc, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining("; "));
        if (msg == null || msg.isEmpty()) {
            msg = "参数校验失败";
        }
        return ApiResponse.fail(ResultCode.VALIDATION_ERROR, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<?> handleConstraintViolation(ConstraintViolationException ex) {
        return ApiResponse.fail(ResultCode.VALIDATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ApiResponse.fail(ResultCode.VALIDATION_ERROR, "请求参数格式错误");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        // 真实项目建议打日志/埋点；此处只返回统一错误结构
        log.error("Unhandled exception", ex);
        ApiResponse<?> body = ApiResponse.fail(ResultCode.SYSTEM_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

