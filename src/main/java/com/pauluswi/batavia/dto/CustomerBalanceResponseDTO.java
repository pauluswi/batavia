package com.pauluswi.batavia.dto;

import lombok.Data;

@Data
public class CustomerBalanceResponseDTO {
    private String MTI;
    private String responseCode;
    private BalanceDataDTO data;
}
