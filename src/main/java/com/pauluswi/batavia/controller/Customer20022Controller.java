package com.pauluswi.batavia.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pauluswi.batavia.service.CustomerBalance20022Service;
import com.pauluswi.dto.BalanceInquiryRequestDTO;
import com.pauluswi.dto.BalanceInquiryResponseDTO;

@RestController
@RequestMapping("/api/20022/customer/balance")
public class Customer20022Controller {

    @Autowired
    private CustomerBalance20022Service customerBalanceService;

    @PostMapping
    public BalanceInquiryResponseDTO getCustomerBalance(@RequestBody BalanceInquiryRequestDTO requestDTO) {
        return customerBalanceService.getCustomerBalance(requestDTO);
    }
    
}
