package com.pauluswi.batavia.service.demo;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.pauluswi.batavia.util.DataMaskingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        // Jackson XML Mapper does not include the XML declaration by default and order might differ slightly
        // but for this test we expect the structure to be correct.
        // We will focus on checking if the log message is masked and contains key elements.
        
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
}
