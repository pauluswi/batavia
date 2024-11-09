package com.pauluswi.batavia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pauluswi.batavia.service.CustomerBalanceService;
import com.pauluswi.dto.BalanceInquiryRequestDTO;
import com.pauluswi.dto.BalanceInquiryResponseDTO;

@RestController
@RequestMapping("/api/customer/balance")
public class CustomerController {

    @Autowired
    private CustomerBalanceService customerBalanceService;

    @PostMapping
    public BalanceInquiryResponseDTO getCustomerBalance(@RequestBody BalanceInquiryRequestDTO requestDTO) {
        return customerBalanceService.getCustomerBalance(requestDTO);
    }
    
}
