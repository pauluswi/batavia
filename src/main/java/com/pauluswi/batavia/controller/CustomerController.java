package com.pauluswi.batavia.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pauluswi.batavia.service.CustomerBalanceService;

@RestController
@RequestMapping("/api/customer/balance")
public class CustomerController {

    @Autowired
    private CustomerBalanceService customerBalanceService;

    @GetMapping("/{customerId}")
    public Map<String, Object> getCustomerBalance(@PathVariable String customerId) {
        return customerBalanceService.getCustomerBalance(customerId);
    }
    
}
