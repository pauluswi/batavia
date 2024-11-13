package com.pauluswi.batavia.dto;

import lombok.Data;

@Data
public class BalanceInquiryRequestDTO {
    private String bankAccountNumber;
    private String customerFullName;
}
