package com.pauluswi.batavia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pauluswi.batavia.service.demo.ISO20022ResponseParser;
import com.pauluswi.batavia.service.demo.ISO20022Service;
import com.pauluswi.dto.BalanceDataDTO;
import com.pauluswi.dto.BalanceInquiryRequestDTO;
import com.pauluswi.dto.BalanceInquiryResponseDTO;

@Service
public class CustomerBalance20022Service {

    @Autowired
    private ISO20022Service iso20022Service;
    
     public BalanceInquiryResponseDTO getCustomerBalance(BalanceInquiryRequestDTO requestDTO) {
        // Step 1: Build ISO 20022 request
        String iso20022Request = iso20022Service.buildBalanceInquiryRequest(
                requestDTO.getBankAccountNumber(), requestDTO.getCustomerFullName());


        // Step 2: Simulate ISO 20022 response
        String iso20022Response = iso20022Service.simulateBalanceInquiryResponse(iso20022Request);


        // Step 3: Parse XML response to DTO
        return parseXmlResponseToDto(iso20022Response);
    }

    private BalanceInquiryResponseDTO parseXmlResponseToDto(String iso20022Response) {
        // Parse XML response and map fields to DTO
        BalanceInquiryResponseDTO responseDTO = new BalanceInquiryResponseDTO();
        responseDTO.setResponseCode("00");
        responseDTO.setMTI("msg123456");

        BalanceDataDTO dataDTO = new BalanceDataDTO();
    //    dataDTO.setBankAccountNumber("123456");
        dataDTO.setBankAccountNumber(ISO20022ResponseParser.getBankAccountNumber(iso20022Response));
        dataDTO.setCustomerFullName(ISO20022ResponseParser.getBankAccountNumber(iso20022Response));
        dataDTO.setBalance(Double.parseDouble(ISO20022ResponseParser.getBalance(iso20022Response)));

        responseDTO.setData(dataDTO);
        return responseDTO;
    }
}
