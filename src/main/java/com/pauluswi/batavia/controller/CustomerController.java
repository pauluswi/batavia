package com.pauluswi.batavia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pauluswi.batavia.dto.CustomerBalanceRequestDTO;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.service.Customer20022Service;
import com.pauluswi.batavia.service.Customer8583Service;

@RestController
@RequestMapping("/api/{protocol}/customer/balance")
public class CustomerController {

    @Autowired
    private Customer8583Service customer8583Service;

    @Autowired
    private Customer20022Service customer20022Service;

    @PostMapping
    public CustomerBalanceResponseDTO getCustomerBalance(@PathVariable String protocol, @RequestBody CustomerBalanceRequestDTO requestDTO) {
        if ("8583".equals(protocol)) {
            return customer8583Service.getCustomerBalance(requestDTO);
        } else if ("20022".equals(protocol)) {
            return customer20022Service.getCustomerBalance(requestDTO);
        } else {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
    }
}
