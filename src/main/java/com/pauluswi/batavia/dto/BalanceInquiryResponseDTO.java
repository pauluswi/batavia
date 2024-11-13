package com.pauluswi.batavia.dto;

import lombok.Data;

@Data
public class BalanceInquiryResponseDTO {
    private String MTI;
    private String responseCode;
    private BalanceDataDTO data;
}

