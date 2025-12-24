package com.pauluswi.batavia.exception;

import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomerBalanceResponseDTO> handleException(Exception e) {
        logger.error("Unhandled exception occurred", e);
        CustomerBalanceResponseDTO response = new CustomerBalanceResponseDTO();
        response.setResponseCode(ErrorCode.SYSTEM_ERROR.getCode());
        // In a real scenario, you might want to set a generic error message or details
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
