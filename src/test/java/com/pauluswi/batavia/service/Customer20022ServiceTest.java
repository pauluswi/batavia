package com.pauluswi.batavia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.CustomerBalanceRequestDTO;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.demo.ISO20022Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Customer20022ServiceTest {

    @Mock
    private ISO20022Service iso20022Service;

    @InjectMocks
    private Customer20022Service customer20022Service;

    private CustomerBalanceRequestDTO requestDTO;

    @BeforeEach
    public void setUp() {
        requestDTO = new CustomerBalanceRequestDTO();
        requestDTO.setBankAccountNumber("1234567890");
        requestDTO.setCustomerFullName("Ahmad Subarjo");
    }

    @Test
    public void testGetCustomerBalance_Success() {
        // Arrange
        String mockXmlRequest = "<request/>";
        // A simplified but valid XML structure for the test
        String mockXmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Document>" +
                "  <CstmrCdtTrfInitn>" +
                "    <GrpHdr><MsgId>msg-test-123</MsgId></GrpHdr>" +
                "    <PmtInf>" +
                "      <DbtrAcct><Id><Othr><Id>1234567890</Id></Othr></Id></DbtrAcct>" +
                "    </PmtInf>" +
                "    <AcctInf><Name>Ahmad Subarjo</Name></AcctInf>" +
                "    <Bal><Amt>1500.00</Amt></Bal>" +
                "  </CstmrCdtTrfInitn>" +
                "</Document>";

        when(iso20022Service.buildBalanceInquiryRequest(anyString(), anyString())).thenReturn(mockXmlRequest);
        when(iso20022Service.simulateBalanceInquiryResponse(anyString())).thenReturn(mockXmlResponse);

        // Act
        CustomerBalanceResponseDTO response = customer20022Service.getCustomerBalance(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(ErrorCode.SUCCESS.getCode(), response.getResponseCode());
        assertEquals("msg-test-123", response.getMTI());

        BalanceDataDTO data = response.getData();
        assertNotNull(data);
        assertEquals("1234567890", data.getBankAccountNumber());
        assertEquals("Ahmad Subarjo", data.getCustomerFullName());
        assertEquals(1500.00, data.getBalance());
    }

    @Test
    public void testGetCustomerBalance_JsonProcessingException() {
        String invalidXml = "<invalid";
        when(iso20022Service.buildBalanceInquiryRequest(anyString(), anyString())).thenReturn("<request/>");
        when(iso20022Service.simulateBalanceInquiryResponse(anyString())).thenReturn(invalidXml);

        assertThrows(RuntimeException.class, () -> {
            customer20022Service.getCustomerBalance(requestDTO);
        });
    }

    @Test
    public void testFallbackGetCustomerBalance() {
        // Arrange
        Throwable throwable = new RuntimeException("Simulated downstream failure");

        // Act
        CustomerBalanceResponseDTO response = customer20022Service.fallbackGetCustomerBalance(requestDTO, throwable);

        // Assert
        assertNotNull(response);
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), response.getResponseCode());
    }
}
