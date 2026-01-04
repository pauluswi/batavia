package com.pauluswi.batavia.service;

import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.dto.FundTransferResponseDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundTransferServiceTest {

    @Mock
    private ISO8583Service iso8583Service;

    @InjectMocks
    private FundTransferService fundTransferService;

    private FundTransferRequestDTO requestDTO;

    @BeforeEach
    public void setUp() {
        requestDTO = new FundTransferRequestDTO();
        requestDTO.setSourceAccountNumber("1234567890");
        requestDTO.setDestinationAccountNumber("0987654321");
        requestDTO.setAmount(100.00);
        requestDTO.setCurrency("IDR");
        requestDTO.setDescription("Test Transfer");
    }

    @Test
    public void testTransferFunds_Success() throws ISOException {
        // Mock ISO Request
        ISOMsg mockIsoRequest = new ISOMsg();
        mockIsoRequest.setMTI("0200");
        
        // Mock ISO Response
        ISOMsg mockIsoResponse = new ISOMsg();
        mockIsoResponse.setMTI("0210");
        mockIsoResponse.set(39, "00"); // Success
        mockIsoResponse.set(37, "RRN123456");

        when(iso8583Service.createFundTransferRequest(anyString(), anyString(), anyDouble()))
                .thenReturn(mockIsoRequest);
        when(iso8583Service.createFundTransferResponse(any(ISOMsg.class)))
                .thenReturn(mockIsoResponse);

        FundTransferResponseDTO response = fundTransferService.transferFunds(requestDTO);

        assertNotNull(response);
        assertEquals(ErrorCode.SUCCESS.getCode(), response.getResponseCode());
        assertEquals("RRN123456", response.getTransactionId());
        assertEquals("Transfer Successful", response.getMessage());
    }

    @Test
    public void testTransferFunds_Failure() throws ISOException {
        // Mock ISO Request
        ISOMsg mockIsoRequest = new ISOMsg();
        mockIsoRequest.setMTI("0200");
        
        // Mock ISO Response (Failure)
        ISOMsg mockIsoResponse = new ISOMsg();
        mockIsoResponse.setMTI("0210");
        mockIsoResponse.set(39, "96"); // System Error
        
        when(iso8583Service.createFundTransferRequest(anyString(), anyString(), anyDouble()))
                .thenReturn(mockIsoRequest);
        when(iso8583Service.createFundTransferResponse(any(ISOMsg.class)))
                .thenReturn(mockIsoResponse);

        FundTransferResponseDTO response = fundTransferService.transferFunds(requestDTO);

        assertNotNull(response);
        assertEquals("96", response.getResponseCode());
        assertEquals("Transfer Failed", response.getMessage());
    }

    @Test
    public void testFallbackFundTransfer() {
        Throwable t = new RuntimeException("Simulated Timeout");
        FundTransferResponseDTO response = fundTransferService.fallbackFundTransfer(requestDTO, t);

        assertNotNull(response);
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), response.getResponseCode());
        assertEquals("Transaction failed due to system error", response.getMessage());
    }
}
