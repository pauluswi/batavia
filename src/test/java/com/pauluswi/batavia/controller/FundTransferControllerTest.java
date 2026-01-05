package com.pauluswi.batavia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.dto.FundTransferResponseDTO;
import com.pauluswi.batavia.service.FundTransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FundTransferController.class)
public class FundTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FundTransferService fundTransferService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testTransferFunds_8583() throws Exception {
        FundTransferRequestDTO requestDTO = new FundTransferRequestDTO();
        requestDTO.setSourceAccountNumber("1234567890");
        requestDTO.setDestinationAccountNumber("0987654321");
        requestDTO.setAmount(100.00);
        requestDTO.setCurrency("IDR");

        FundTransferResponseDTO responseDTO = new FundTransferResponseDTO();
        responseDTO.setResponseCode("00");
        responseDTO.setTransactionId("RRN123456");
        responseDTO.setMessage("Transfer Successful");

        when(fundTransferService.transferFunds(eq("8583"), any(FundTransferRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/8583/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("00"))
                .andExpect(jsonPath("$.transactionId").value("RRN123456"))
                .andExpect(jsonPath("$.message").value("Transfer Successful"));
    }

    @Test
    public void testTransferFunds_20022() throws Exception {
        FundTransferRequestDTO requestDTO = new FundTransferRequestDTO();
        requestDTO.setSourceAccountNumber("1234567890");
        requestDTO.setDestinationAccountNumber("0987654321");
        requestDTO.setAmount(100.00);
        requestDTO.setCurrency("IDR");

        FundTransferResponseDTO responseDTO = new FundTransferResponseDTO();
        responseDTO.setResponseCode("00");
        responseDTO.setTransactionId("TRX20022");
        responseDTO.setMessage("Transfer Successful");

        when(fundTransferService.transferFunds(eq("20022"), any(FundTransferRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/20022/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("00"))
                .andExpect(jsonPath("$.transactionId").value("TRX20022"))
                .andExpect(jsonPath("$.message").value("Transfer Successful"));
    }
}
