package com.pauluswi.batavia.controller;

import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.CustomerBalanceRequestDTO;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.service.Customer20022Service;
import com.pauluswi.batavia.service.Customer8583Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Customer8583Service customer8583Service;

    @MockBean
    private Customer20022Service customer20022Service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCustomerBalance8583() throws Exception {
        // Set up the mock response from the service
        CustomerBalanceRequestDTO requestDTO = new CustomerBalanceRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");

        CustomerBalanceResponseDTO responseDTO = new CustomerBalanceResponseDTO();
        responseDTO.setResponseCode("00");
        responseDTO.setMTI("0210");

        BalanceDataDTO responseDataDTO = new BalanceDataDTO();
        responseDataDTO.setBankAccountNumber("1234567890");
        responseDataDTO.setCustomerFullName("Ahmad Subarjo");
        responseDataDTO.setBalance(1500.00);

        responseDTO.setData(responseDataDTO);

        // Configure the service mock
        when(customer8583Service.getCustomerBalance(requestDTO)).thenReturn(responseDTO);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/8583/customer/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("00"))
                .andExpect(jsonPath("$.mti").value("0210"))
                .andExpect(jsonPath("$.data.bankAccountNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.customerFullName").value("Ahmad Subarjo"))
                .andExpect(jsonPath("$.data.balance").value(1500.00));
    }

    @Test
    public void testGetCustomerBalance20022() throws Exception {
        // Arrange
        CustomerBalanceRequestDTO requestDTO = new CustomerBalanceRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");
        requestDTO.setCustomerFullName("Ahmad Subarjo");

        BalanceDataDTO dataDTO = new BalanceDataDTO();
        dataDTO.setBankAccountNumber("1234567890");
        dataDTO.setCustomerFullName("Ahmad Subarjo");
        dataDTO.setBalance(1500.00);

        CustomerBalanceResponseDTO responseDTO = new CustomerBalanceResponseDTO();
        responseDTO.setResponseCode("00");
        responseDTO.setMTI("msg123456");
        responseDTO.setData(dataDTO);

        // Mock the service response
        when(customer20022Service.getCustomerBalance(requestDTO)).thenReturn(responseDTO);

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

    @Test
    public void testGetCustomerBalance_UnsupportedProtocol() throws Exception {
        CustomerBalanceRequestDTO requestDTO = new CustomerBalanceRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");

        mockMvc.perform(post("/api/9999/customer/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Unsupported protocol: 9999")));
    }
}
