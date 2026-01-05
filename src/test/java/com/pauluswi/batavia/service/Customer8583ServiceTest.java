package com.pauluswi.batavia.service;

import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.CustomerBalanceRequestDTO;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.demo.ISO8583Service;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Customer8583ServiceTest {

    @Mock
    private ISO8583Service iso8583Service;

    @InjectMocks
    private Customer8583Service customer8583Service;

    private CustomerBalanceRequestDTO requestDTO;

    @BeforeEach
    public void setUp() {
        requestDTO = new CustomerBalanceRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");
        requestDTO.setCustomerFullName("Ahmad Subarjo");
    }

    @Test
    public void testGetCustomerBalance_Success() throws ISOException {
        // Arrange
        ISOMsg isoRequest = new ISOMsg();
        ISOMsg isoResponse = new ISOMsg();
        isoResponse.setMTI("0210");
        isoResponse.set(39, "00");
        isoResponse.set(102, "1234567890");
        isoResponse.set(103, "Ahmad Subarjo");
        isoResponse.set(54, "1500.00");

        when(iso8583Service.createBalanceInquiryRequest(anyString(), anyString())).thenReturn(isoRequest);
        when(iso8583Service.createBalanceInquiryResponse(any(ISOMsg.class))).thenReturn(isoResponse);

        // Act
        CustomerBalanceResponseDTO responseDTO = customer8583Service.getCustomerBalance(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("0210", responseDTO.getMTI());
        assertEquals("00", responseDTO.getResponseCode());

        BalanceDataDTO dataDTO = responseDTO.getData();
        assertEquals("1234567890", dataDTO.getBankAccountNumber());
        assertEquals("Ahmad Subarjo", dataDTO.getCustomerFullName());
        assertEquals(1500.00, dataDTO.getBalance());
    }

    @Test
    public void testGetCustomerBalance_ISOException() throws ISOException {
        when(iso8583Service.createBalanceInquiryRequest(anyString(), anyString())).thenThrow(new ISOException("Test ISO Exception"));

        assertThrows(RuntimeException.class, () -> {
            customer8583Service.getCustomerBalance(requestDTO);
        });
    }

    @Test
    public void testFallbackGetCustomerBalance() {
        Throwable t = new RuntimeException("Simulated Timeout");
        CustomerBalanceResponseDTO response = customer8583Service.fallbackGetCustomerBalance(requestDTO, t);

        assertNotNull(response);
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), response.getResponseCode());
    }
}
