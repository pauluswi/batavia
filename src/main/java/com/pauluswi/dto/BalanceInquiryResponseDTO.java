package com.pauluswi.dto;

import lombok.Data;

@Data
public class BalanceInquiryResponseDTO {
    private String MTI;
    private String responseCode;
    private BalanceDataDTO data;
}

