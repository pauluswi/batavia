package com.pauluswi.batavia.dto;

import lombok.Data;

@Data
public class BalanceDataDTO {
    private String bankAccountNumber;
    private String customerFullName;
    private double balance;
}
