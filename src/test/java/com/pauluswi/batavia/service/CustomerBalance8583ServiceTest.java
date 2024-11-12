package com.pauluswi.batavia.service;

import com.pauluswi.batavia.service.demo.ISO8583Service;
import com.pauluswi.dto.BalanceDataDTO;
import com.pauluswi.dto.BalanceInquiryRequestDTO;
import com.pauluswi.dto.BalanceInquiryResponseDTO;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CustomerBalance8583ServiceTest {

    @Mock
    private ISO8583Service iso8583Service;

    @InjectMocks
    private CustomerBalance8583Service customerBalanceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCustomerBalance() throws ISOException {
        // Arrange
        BalanceInquiryRequestDTO requestDTO = new BalanceInquiryRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");
        requestDTO.setCustomerFullName("Ahmad Subarjo");

        ISOMsg isoRequest = new ISOMsg();
        isoRequest.setMTI("0200");
        isoRequest.set(102, "1234567890");
        isoRequest.set(103, "Ahmad Subarjo");

        ISOMsg isoResponse = new ISOMsg();
        isoResponse.setMTI("0210");
        isoResponse.set(39, "00");
        isoResponse.set(102, "1234567890");
        isoResponse.set(103, "Ahmad Subarjo");
        isoResponse.set(54, "1500.00");

        // Mock the ISO8583Service methods
        when(iso8583Service.createBalanceInquiryRequest("1234567890", "Ahmad Subarjo")).thenReturn(isoRequest);
        when(iso8583Service.createBalanceInquiryResponse(isoRequest)).thenReturn(isoResponse);

        // Act
        BalanceInquiryResponseDTO responseDTO = customerBalanceService.getCustomerBalance(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("0210", responseDTO.getMTI());
        assertEquals("00", responseDTO.getResponseCode());

        BalanceDataDTO dataDTO = responseDTO.getData();
        assertEquals("1234567890", dataDTO.getBankAccountNumber());
        assertEquals("Ahmad Subarjo", dataDTO.getCustomerFullName());
        assertEquals(1500.00, dataDTO.getBalance());
    }
}
