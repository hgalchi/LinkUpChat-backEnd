package com.example.security.common.exception;

import com.example.security.common.codes.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ResponseCode errorcode;

    public BusinessException(ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorcode = errorCode;
    }

    public BusinessException(ResponseCode errorCode, String message) {
        super(message);
        this.errorcode = errorCode;
    }
}
