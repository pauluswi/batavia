package com.pauluswi.batavia.dto;

import lombok.Data;

@Data
public class FundTransferRequestDTO {
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private double amount;
    private String currency;
    private String description;
}
