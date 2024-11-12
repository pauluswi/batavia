package com.pauluswi.batavia.service;

import com.pauluswi.batavia.service.demo.ISO20022ResponseParser;
import com.pauluswi.batavia.service.demo.ISO20022Service;
import com.pauluswi.dto.BalanceDataDTO;
import com.pauluswi.dto.BalanceInquiryRequestDTO;
import com.pauluswi.dto.BalanceInquiryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CustomerBalance20022ServiceTest {

    @Mock
    private ISO20022Service iso20022Service;

    @InjectMocks
    private CustomerBalance20022Service customerBalanceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCustomerBalance() {
        // Arrange
        BalanceInquiryRequestDTO requestDTO = new BalanceInquiryRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");
        requestDTO.setCustomerFullName("Ahmad Subarjo");

        String iso20022Request = "<ISO20022 request XML>";
        String iso20022Response = "<ISO20022 response XML>";

        // Mock the methods of ISO20022Service
        when(iso20022Service.buildBalanceInquiryRequest("1234567890", "Ahmad Subarjo")).thenReturn(iso20022Request);
        when(iso20022Service.simulateBalanceInquiryResponse(iso20022Request)).thenReturn(iso20022Response);

        // Mock the static methods of ISO20022ResponseParser
        try (MockedStatic<ISO20022ResponseParser> mockedParser = mockStatic(ISO20022ResponseParser.class)) {
            mockedParser.when(() -> ISO20022ResponseParser.getBankAccountNumber(iso20022Response)).thenReturn("1234567890");
            mockedParser.when(() -> ISO20022ResponseParser.getCustomerFullName(iso20022Response)).thenReturn("Ahmad Subarjo");
            mockedParser.when(() -> ISO20022ResponseParser.getBalance(iso20022Response)).thenReturn("1500.00");

            // Act
            BalanceInquiryResponseDTO responseDTO = customerBalanceService.getCustomerBalance(requestDTO);

            // Assert
            assertEquals("00", responseDTO.getResponseCode());
            assertEquals("msg123456", responseDTO.getMTI());

            BalanceDataDTO dataDTO = responseDTO.getData();
            assertEquals("1234567890", dataDTO.getBankAccountNumber());
            assertEquals(1500.00, dataDTO.getBalance());
        }
    }
}
