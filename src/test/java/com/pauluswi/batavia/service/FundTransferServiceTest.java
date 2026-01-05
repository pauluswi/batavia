package com.pauluswi.batavia.service;

import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.dto.FundTransferResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.demo.ISO20022Service;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundTransferServiceTest {

    @Mock
    private ISO8583Service iso8583Service;

    @Mock
    private ISO20022Service iso20022Service;

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
    public void testTransferFunds_8583_Success() throws ISOException {
        // Mock ISO 8583
        ISOMsg mockIsoRequest = new ISOMsg();
        ISOMsg mockIsoResponse = new ISOMsg();
        mockIsoResponse.set(39, "00");
        mockIsoResponse.set(37, "RRN123456");

        when(iso8583Service.createFundTransferRequest(anyString(), anyString(), anyDouble())).thenReturn(mockIsoRequest);
        when(iso8583Service.createFundTransferResponse(any(ISOMsg.class))).thenReturn(mockIsoResponse);

        FundTransferResponseDTO response = fundTransferService.transferFunds("8583", requestDTO);

        assertNotNull(response);
        assertEquals(ErrorCode.SUCCESS.getCode(), response.getResponseCode());
        assertEquals("RRN123456", response.getTransactionId());
        assertEquals("Transfer Successful", response.getMessage());
    }

    @Test
    public void testTransferFunds_8583_Failure() throws ISOException {
        when(iso8583Service.createFundTransferRequest(anyString(), anyString(), anyDouble())).thenThrow(new ISOException("Test Failure"));

        assertThrows(RuntimeException.class, () -> {
            fundTransferService.transferFunds("8583", requestDTO);
        });
    }

    @Test
    public void testTransferFunds_20022_Success() {
        // Mock ISO 20022
        String mockXmlRequest = "<request/>";
        String mockXmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Document><FIToFIPmtStsRpt><TxInfAndSts><TxSts>ACSC</TxSts><TxId>TRX20022</TxId></TxInfAndSts></FIToFIPmtStsRpt></Document>";

        when(iso20022Service.buildFundTransferRequest(any(FundTransferRequestDTO.class))).thenReturn(mockXmlRequest);
        when(iso20022Service.simulateFundTransferResponse(anyString())).thenReturn(mockXmlResponse);

        FundTransferResponseDTO response = fundTransferService.transferFunds("20022", requestDTO);

        assertNotNull(response);
        assertEquals(ErrorCode.SUCCESS.getCode(), response.getResponseCode());
        assertEquals("TRX20022", response.getTransactionId());
        assertEquals("Transfer Successful", response.getMessage());
    }
    
    @Test
    public void testTransferFunds_20022_Failure() {
        when(iso20022Service.buildFundTransferRequest(any(FundTransferRequestDTO.class))).thenThrow(new RuntimeException("Test Failure"));

        assertThrows(RuntimeException.class, () -> {
            fundTransferService.transferFunds("20022", requestDTO);
        });
    }

    @Test
    public void testTransferFunds_UnsupportedProtocol() {
        assertThrows(IllegalArgumentException.class, () -> {
            fundTransferService.transferFunds("9999", requestDTO);
        });
    }

    @Test
    public void testFallbackFundTransfer() {
        Throwable t = new RuntimeException("Simulated Timeout");
        FundTransferResponseDTO response = fundTransferService.fallbackFundTransfer("8583", requestDTO, t);

        assertNotNull(response);
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), response.getResponseCode());
        assertEquals("Transaction failed due to system error", response.getMessage());
    }
}
