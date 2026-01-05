package com.pauluswi.batavia.aspect;

import com.pauluswi.batavia.annotation.Idempotent;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdempotencyAspectTest {

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Idempotent idempotentAnnotation;

    @InjectMocks
    private IdempotencyAspect idempotencyAspect;

    @BeforeEach
    public void setUp() {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
        when(idempotentAnnotation.headerName()).thenReturn("X-Request-ID");
    }

    @Test
    public void testCheckIdempotency_MissingHeader() throws Throwable {
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(joinPoint.proceed()).thenReturn("Success");

        Object result = idempotencyAspect.checkIdempotency(joinPoint, idempotentAnnotation);

        assertEquals("Success", result);
        verify(idempotencyService, never()).contains(anyString());
    }

    @Test
    public void testCheckIdempotency_DuplicateRequest() throws Throwable {
        String requestId = "12345";
        when(request.getHeader("X-Request-ID")).thenReturn(requestId);
        when(idempotencyService.contains(requestId)).thenReturn(true);

        Object result = idempotencyAspect.checkIdempotency(joinPoint, idempotentAnnotation);

        CustomerBalanceResponseDTO response = (CustomerBalanceResponseDTO) result;
        assertEquals(ErrorCode.DUPLICATE_TRANSACTION.getCode(), response.getResponseCode());
        verify(joinPoint, never()).proceed();
    }

    @Test
    public void testCheckIdempotency_NewRequest() throws Throwable {
        String requestId = "12345";
        when(request.getHeader("X-Request-ID")).thenReturn(requestId);
        when(idempotencyService.contains(requestId)).thenReturn(false);
        when(joinPoint.proceed()).thenReturn("Success");

        Object result = idempotencyAspect.checkIdempotency(joinPoint, idempotentAnnotation);

        assertEquals("Success", result);
        verify(idempotencyService).put(requestId, "Success");
    }
}
