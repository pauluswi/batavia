package com.pauluswi.dto;

import lombok.Data;

@Data
public class BalanceInquiryRequestDTO {
    private String bankAccountNumber;
    private String customerFullName;
}
