package com.pauluswi.batavia.controller;

import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.BalanceInquiryRequestDTO;
import com.pauluswi.batavia.dto.BalanceInquiryResponseDTO;
import com.pauluswi.batavia.service.CustomerBalance20022Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(Customer20022Controller.class)
public class Customer20022ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerBalance20022Service customerBalanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCustomerBalance() throws Exception {
        // Arrange
        BalanceInquiryRequestDTO requestDTO = new BalanceInquiryRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");
        requestDTO.setCustomerFullName("Ahmad Subarjo");

        BalanceDataDTO dataDTO = new BalanceDataDTO();
        dataDTO.setBankAccountNumber("1234567890");
        dataDTO.setCustomerFullName("Ahmad Subarjo");
        dataDTO.setBalance(1500.00);

        BalanceInquiryResponseDTO responseDTO = new BalanceInquiryResponseDTO();
        responseDTO.setResponseCode("00");
        responseDTO.setMTI("msg123456");
        responseDTO.setData(dataDTO);

        // Mock the service response
        when(customerBalanceService.getCustomerBalance(requestDTO)).thenReturn(responseDTO);

        // Act and Assert
        mockMvc.perform(post("/api/20022/customer/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("00"))
                .andExpect(jsonPath("$.mti").value("msg123456"))
                .andExpect(jsonPath("$.data.bankAccountNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.customerFullName").value("Ahmad Subarjo"))
                .andExpect(jsonPath("$.data.balance").value(1500.00));
    }
}
