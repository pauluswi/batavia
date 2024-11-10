package com.pauluswi.batavia.service.demo;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
        String customerFullName = "John Doe";

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\">"
                + "<CstmrCdtTrfInitn>"
                + "<GrpHdr><MsgId>msg123456</MsgId><CreDtTm>2024-01-01T12:00:00</CreDtTm></GrpHdr>"
                + "<PmtInf>"
                + "<Dbtr><Nm>John Doe</Nm></Dbtr>"
                + "<DbtrAcct><Id><Othr><Id>123456789</Id></Othr></Id></DbtrAcct>"
                + "</PmtInf>"
                + "</CstmrCdtTrfInitn>"
                + "</Document>";

        String result = iso20022Service.buildBalanceInquiryRequest(bankAccountNumber, customerFullName);
        
        assertEquals(expectedXml, result, "The generated XML for balance inquiry request does not match the expected output.");

        // Verify logger output
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size(), "Expected exactly one log entry.");
        assertEquals("ISO 20022 Request Message: " + expectedXml, logsList.get(0).getFormattedMessage());
    }

    @Test
    public void testSimulateBalanceInquiryResponse() {
        String requestXml = "<some-xml-request>"; // Just a placeholder, since the request XML is not used in this method

        String expectedResponseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\">"
                + "<CstmrCdtTrfInitn>"
                + "<GrpHdr><MsgId>msg123456</MsgId><CreDtTm>2024-01-01T12:00:00</CreDtTm></GrpHdr>"
                + "<PmtInf><DbtrAcct><Id><Othr><Id>123456</Id></Othr></Id></DbtrAcct></PmtInf>"
                + "<Bal><Amt Ccy=\"USD\">1500.00</Amt></Bal>"
                + "<AcctInf><CIF>111</CIF><Name>Andi Lukito</Name></AcctInf>"
                + "</CstmrCdtTrfInitn>"
                + "</Document>";

        String result = iso20022Service.simulateBalanceInquiryResponse(requestXml);
        
        assertEquals(expectedResponseXml, result, "The simulated XML response does not match the expected output.");

        // Verify logger output
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size(), "Expected exactly two log entries.");
        assertEquals("ISO 20022 Response Message: " + expectedResponseXml, logsList.get(0).getFormattedMessage());
    }
}
