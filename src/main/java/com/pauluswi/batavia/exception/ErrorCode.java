package com.pauluswi.batavia.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS("00", "Success"),
    INVALID_REQUEST("14", "Invalid Request"),
    SYSTEM_ERROR("96", "System Error"),
    TIMEOUT("68", "Request Timed Out"),
    DUPLICATE_TRANSACTION("94", "Duplicate Transaction");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
