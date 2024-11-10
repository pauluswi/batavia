package com.pauluswi.batavia.service.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ISO20022Service {

    private static final Logger logger = LoggerFactory.getLogger(ISO8583Service.class);


    public String buildBalanceInquiryRequest(String bankAccountNumber, String customerFullName) {
        // Construct a simple ISO 20022-like XML message for balance inquiry
        String requestData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\">"
                + "<CstmrCdtTrfInitn>"
                + "<GrpHdr><MsgId>msg123456</MsgId><CreDtTm>2024-01-01T12:00:00</CreDtTm></GrpHdr>"
                + "<PmtInf>"
                + "<Dbtr><Nm>" + customerFullName + "</Nm></Dbtr>"
                + "<DbtrAcct><Id><Othr><Id>" + bankAccountNumber + "</Id></Othr></Id></DbtrAcct>"
                + "</PmtInf>"
                + "</CstmrCdtTrfInitn>"
                + "</Document>";

        // Log the request message
        logger.info("ISO 20022 Request Message: {}", requestData);

        return requestData;

    }

    public String simulateBalanceInquiryResponse(String requestXml) {
        // Construct a simple ISO 20022-like XML response message with mock data
        String responseData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\">"
                + "<CstmrCdtTrfInitn>"
                + "<GrpHdr><MsgId>msg123456</MsgId><CreDtTm>2024-01-01T12:00:00</CreDtTm></GrpHdr>"
                + "<PmtInf><DbtrAcct><Id><Othr><Id>123456</Id></Othr></Id></DbtrAcct></PmtInf>"
                + "<Bal><Amt Ccy=\"USD\">1500.00</Amt></Bal>"
                + "<AcctInf><CIF>111</CIF><Name>Andi Lukito</Name></AcctInf>"
                + "</CstmrCdtTrfInitn>"
                + "</Document>";

        // Log the request message
        logger.info("ISO 20022 Response Message: {}", responseData);

        return responseData;
    }
}
