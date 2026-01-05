package com.pauluswi.batavia.exception;

import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleException() {
        Exception exception = new RuntimeException("Test Exception");
        ResponseEntity<CustomerBalanceResponseDTO> response = exceptionHandler.handleException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), response.getBody().getResponseCode());
    }
}
