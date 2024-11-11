package com.pauluswi.batavia.controller;

import com.pauluswi.batavia.service.CustomerBalance8583Service;
import com.pauluswi.dto.BalanceDataDTO;
import com.pauluswi.dto.BalanceInquiryRequestDTO;
import com.pauluswi.dto.BalanceInquiryResponseDTO;
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

@WebMvcTest(Customer8583Controller.class)
public class Customer8583ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerBalance8583Service customerBalanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCustomerBalance() throws Exception {
        // Set up the mock response from the service
        BalanceInquiryRequestDTO requestDTO = new BalanceInquiryRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");

        BalanceInquiryResponseDTO responseDTO = new BalanceInquiryResponseDTO();
        responseDTO.setResponseCode("00");
        responseDTO.setMTI("0210");

        BalanceDataDTO responseDataDTO = new BalanceDataDTO();
        responseDataDTO.setBankAccountNumber("1234567890");
        responseDataDTO.setCustomerFullName("John Doe");
        responseDataDTO.setBalance(1500.00);

        responseDTO.setData(responseDataDTO);

        // Configure the service mock
        when(customerBalanceService.getCustomerBalance(requestDTO)).thenReturn(responseDTO);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/8583/customer/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("00"))
                .andExpect(jsonPath("$.mti").value("0210"))
                .andExpect(jsonPath("$.data.bankAccountNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.customerFullName").value("John Doe"))
                .andExpect(jsonPath("$.data.balance").value(1500.00));
    }
}
