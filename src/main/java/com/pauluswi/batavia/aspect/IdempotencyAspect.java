package com.pauluswi.batavia.aspect;

import com.pauluswi.batavia.annotation.Idempotent;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.service.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class IdempotencyAspect {

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyAspect.class);
    
    @Autowired
    private IdempotencyService idempotencyService;

    @Around("@annotation(idempotent)")
    public Object checkIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestId = request.getHeader(idempotent.headerName());

        if (requestId == null || requestId.isEmpty()) {
            logger.warn("Missing idempotency key in header: {}", idempotent.headerName());
            // Depending on requirements, we might reject or proceed. 
            // For now, proceed but log warning.
            return joinPoint.proceed();
        }

        if (idempotencyService.contains(requestId)) {
            logger.info("Duplicate request detected for ID: {}", requestId);
            // Return cached response or error indicating duplicate
            // For simplicity, returning a specific error response or the cached object if we stored it
            // Here we just return a duplicate transaction error
            CustomerBalanceResponseDTO response = new CustomerBalanceResponseDTO();
            response.setResponseCode(ErrorCode.DUPLICATE_TRANSACTION.getCode());
            return response;
        }

        Object result = joinPoint.proceed();
        idempotencyService.put(requestId, result);
        return result;
    }
}
