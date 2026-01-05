package com.pauluswi.batavia.service.demo;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ISO8583ServiceTest {

    private ISO8583Service iso8583Service;

    @BeforeEach
    public void setUp() {
        iso8583Service = new ISO8583Service();
    }

    @Test
    public void testCreateBalanceInquiryRequest() throws ISOException {
        ISOMsg msg = iso8583Service.createBalanceInquiryRequest("123456", "John Doe");

        assertEquals("0200", msg.getMTI());
        assertEquals("310000", msg.getString(3));
        assertEquals("123456", msg.getString(102));
        assertEquals("John Doe", msg.getString(103));
    }

    @Test
    public void testCreateBalanceInquiryResponse() throws ISOException {
        ISOMsg request = new ISOMsg();
        request.set(102, "123456");
        request.set(103, "John Doe");

        ISOMsg response = iso8583Service.createBalanceInquiryResponse(request);

        assertEquals("0210", response.getMTI());
        assertEquals("00", response.getString(39));
        assertEquals("123456", response.getString(102));
        assertNotNull(response.getString(54)); // Balance
    }

    @Test
    public void testCreateFundTransferRequest() throws ISOException {
        ISOMsg msg = iso8583Service.createFundTransferRequest("ACC1", "ACC2", 150.00);

        assertEquals("0200", msg.getMTI());
        assertEquals("400000", msg.getString(3));
        assertEquals("000000015000", msg.getString(4)); // Amount
        assertEquals("ACC1", msg.getString(102));
        assertEquals("ACC2", msg.getString(103));
    }

    @Test
    public void testCreateFundTransferResponse() throws ISOException {
        ISOMsg request = new ISOMsg();
        ISOMsg response = iso8583Service.createFundTransferResponse(request);

        assertEquals("0210", response.getMTI());
        assertEquals("00", response.getString(39));
        assertNotNull(response.getString(37)); // RRN
    }
}
