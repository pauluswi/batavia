package com.pauluswi.batavia.service.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ISO20022ResponseParserTest {

    private final String sampleXmlResponse = """
            <Document>
                <Acct>
                    <Id>123456789</Id>
                    <Name>Ahmad Subarjo</Name>
                    <Amt>5000.00</Amt>
                </Acct>
            </Document>
            """;

    @Test
    public void testGetBalance() {
        String balance = ISO20022ResponseParser.getBalance(sampleXmlResponse);
        assertEquals("5000.00", balance, "Balance should match the expected value from XML.");
    }

    @Test
    public void testGetCustomerName() {
        String customerName = ISO20022ResponseParser.getCustomerFullName(sampleXmlResponse);
        assertEquals("Ahmad Subarjo", customerName, "Customer name should match the expected value from XML.");
    }

    @Test
    public void testGetBankAccountNumber() {
        String bankAccountNumber = ISO20022ResponseParser.getBankAccountNumber(sampleXmlResponse);
        assertEquals("123456789", bankAccountNumber, "Bank account number should match the expected value from XML.");
    }

    @Test
    public void testInvalidXmlResponse() {
        String invalidXmlResponse = "<InvalidXml>";
        
        assertNull(ISO20022ResponseParser.getBalance(invalidXmlResponse), "Balance should be null for invalid XML.");
        assertNull(ISO20022ResponseParser.getCustomerFullName(invalidXmlResponse), "Customer name should be null for invalid XML.");
        assertNull(ISO20022ResponseParser.getBankAccountNumber(invalidXmlResponse), "Bank account number should be null for invalid XML.");
    }

    @Test
    public void testMissingFieldsInXml() {
        String incompleteXmlResponse = """
                <Document>
                    <Acct>
                        <Name>Ahmad Subarjo</Name>
                    </Acct>
                </Document>
                """;
        
        assertNull(ISO20022ResponseParser.getBalance(incompleteXmlResponse), "Balance should be null if Amt tag is missing.");
        assertEquals("Ahmad Subarjo", ISO20022ResponseParser.getCustomerFullName(incompleteXmlResponse), "Customer name should still be parsed correctly.");
        assertNull(ISO20022ResponseParser.getBankAccountNumber(incompleteXmlResponse), "Bank account number should be null if Id tag is missing.");
    }
}
