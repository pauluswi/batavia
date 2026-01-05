package com.pauluswi.batavia.service.demo;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.util.DataMaskingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ISO20022ServiceTest {

    private ISO20022Service iso20022Service;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setUp() {
        iso20022Service = new ISO20022Service();

        // Set up a ListAppender to capture logs
        Logger logger = (Logger) LoggerFactory.getLogger(ISO20022Service.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    public void testBuildBalanceInquiryRequest() {
        String bankAccountNumber = "123456789";
        String customerFullName = "Ahmad Subarjo";

        String result = iso20022Service.buildBalanceInquiryRequest(bankAccountNumber, customerFullName);
        
        // Verify logger output
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size(), "Expected exactly one log entry.");
        
        // The log message should be masked
        String expectedLogMessage = "ISO 20022 Request Message: " + DataMaskingUtil.maskIso20022(result);
        assertEquals(expectedLogMessage, logsList.get(0).getFormattedMessage());
    }

    @Test
    public void testSimulateBalanceInquiryResponse() {
        String requestXml = "<some-xml-request>"; 

        String result = iso20022Service.simulateBalanceInquiryResponse(requestXml);
        
        // Verify logger output
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size(), "Expected exactly two log entries.");
        
        // The log message should be masked
        String expectedLogMessage = "ISO 20022 Response Message: " + DataMaskingUtil.maskIso20022(result);
        assertEquals(expectedLogMessage, logsList.get(0).getFormattedMessage());
    }

    @Test
    public void testBuildFundTransferRequest() {
        FundTransferRequestDTO requestDTO = new FundTransferRequestDTO();
        requestDTO.setSourceAccountNumber("1234567890");
        requestDTO.setDestinationAccountNumber("0987654321");
        requestDTO.setAmount(100.00);
        requestDTO.setCurrency("IDR");

        String result = iso20022Service.buildFundTransferRequest(requestDTO);

        assertNotNull(result);
        assertTrue(result.contains("1234567890"));
        assertTrue(result.contains("0987654321"));

        // Verify logger output
        List<ILoggingEvent> logsList = listAppender.list;
        // Depending on test execution order, we might have previous logs. 
        // We check the last log entry.
        ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
        
        String expectedLogMessage = "ISO 20022 Fund Transfer Request: " + DataMaskingUtil.maskIso20022(result);
        assertEquals(expectedLogMessage, lastLog.getFormattedMessage());
    }

    @Test
    public void testSimulateFundTransferResponse() {
        String requestXml = "<some-xml-request>";

        String result = iso20022Service.simulateFundTransferResponse(requestXml);

        assertNotNull(result);
        assertTrue(result.contains("ACSC")); // AcceptedSettlementCompleted

        // Verify logger output
        List<ILoggingEvent> logsList = listAppender.list;
        ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
        
        // Response doesn't have sensitive data to mask in this simulation, but we check the log format
        String expectedLogMessage = "ISO 20022 Fund Transfer Response: " + DataMaskingUtil.maskIso20022(result);
        assertEquals(expectedLogMessage, lastLog.getFormattedMessage());
    }
}
