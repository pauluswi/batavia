package com.pauluswi.batavia.controller;

import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.dto.FundTransferResponseDTO;
import com.pauluswi.batavia.service.FundTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{protocol}/transfer")
public class FundTransferController {

    @Autowired
    private FundTransferService fundTransferService;

    @PostMapping
    public FundTransferResponseDTO transferFunds(@PathVariable String protocol, @RequestBody FundTransferRequestDTO requestDTO) {
        return fundTransferService.transferFunds(protocol, requestDTO);
    }
}
