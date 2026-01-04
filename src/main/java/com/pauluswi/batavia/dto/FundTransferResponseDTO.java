package com.pauluswi.batavia.dto;

import lombok.Data;

@Data
public class FundTransferResponseDTO {
    private String responseCode;
    private String transactionId;
    private String message;
}
