package com.pauluswi.batavia.dto;

import lombok.Data;

@Data
public class CustomerBalanceRequestDTO {
    private String bankAccountNumber;
    private String customerFullName;
}
