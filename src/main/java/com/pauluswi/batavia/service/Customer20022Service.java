package com.pauluswi.batavia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.CustomerBalanceRequestDTO;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.demo.ISO20022ResponseParser;
import com.pauluswi.batavia.service.demo.ISO20022Service;

@Service
public class Customer20022Service {

    private static final Logger logger = LoggerFactory.getLogger(Customer20022Service.class);

    @Autowired
    private ISO20022Service iso20022Service;
    
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CustomerBalanceResponseDTO getCustomerBalance(CustomerBalanceRequestDTO requestDTO) {
        logger.info("Processing ISO 20022 balance inquiry for account: {}", requestDTO.getBankAccountNumber());
        
        // Step 1: Build ISO 20022 request
        String iso20022Request = iso20022Service.buildBalanceInquiryRequest(
                requestDTO.getBankAccountNumber(), requestDTO.getCustomerFullName());


        // Step 2: Simulate ISO 20022 response
        String iso20022Response = iso20022Service.simulateBalanceInquiryResponse(iso20022Request);


        // Step 3: Parse XML response to DTO
        return parseXmlResponseToDto(iso20022Response);
    }

    private CustomerBalanceResponseDTO parseXmlResponseToDto(String iso20022Response) {
        // Parse XML response and map fields to DTO
        CustomerBalanceResponseDTO responseDTO = new CustomerBalanceResponseDTO();
        responseDTO.setResponseCode(ErrorCode.SUCCESS.getCode());
        responseDTO.setMTI("msg123456");

        BalanceDataDTO dataDTO = new BalanceDataDTO();
        dataDTO.setBankAccountNumber(ISO20022ResponseParser.getBankAccountNumber(iso20022Response));
        dataDTO.setCustomerFullName(ISO20022ResponseParser.getBankAccountNumber(iso20022Response));
        dataDTO.setBalance(Double.parseDouble(ISO20022ResponseParser.getBalance(iso20022Response)));

        responseDTO.setData(dataDTO);
        return responseDTO;
    }
}
