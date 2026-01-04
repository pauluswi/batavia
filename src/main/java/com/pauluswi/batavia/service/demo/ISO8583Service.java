package com.pauluswi.batavia.service.demo;

import com.pauluswi.batavia.util.DataMaskingUtil;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO87APackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ISO8583Service {

    private static final Logger logger = LoggerFactory.getLogger(ISO8583Service.class);

    /**
     * Creates an ISO 8583 request message for balance inquiry.
     *
     * @param bankAccountNumber The bank account number.
     * @param customerFullName The customer full name.
     * @return The ISO 8583 message.
     * @throws ISOException If there is an error creating the message.
     */
    public ISOMsg createBalanceInquiryRequest(String bankAccountNumber, String customerFullName) throws ISOException {
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(new ISO87APackager());
        isoMsg.setMTI("0200");
        isoMsg.set(3, "310000"); // Processing code for balance inquiry
        isoMsg.set(4, "000000000000"); // Transaction amount (zero for inquiry)
        isoMsg.set(7, "1111241830"); // Transmission date & time
        isoMsg.set(11, "123456"); // System trace audit number (STAN)
        isoMsg.set(41, "12345678"); // Card acceptor terminal ID
        isoMsg.set(49, "360"); // Currency code (e.g., IDR for Indonesia)
        isoMsg.set(102, bankAccountNumber); // Bank Account Number
        isoMsg.set(103, customerFullName); // Customer Full Name

        // Log the request message with masking
        logger.info("ISO 8583 Request Message: {}", DataMaskingUtil.maskIso8583Log(isoMsgToString(isoMsg)));

        return isoMsg;
    }

    /**
     * Simulates a core system response for a balance inquiry request.
     *
     * @param request The ISO 8583 request message.
     * @return The ISO 8583 response message.
     * @throws ISOException If there is an error processing the message.
     */
    public ISOMsg createBalanceInquiryResponse(ISOMsg request) throws ISOException {
        ISOMsg response = (ISOMsg) request.clone();
        response.setMTI("0210");
        response.set(39, "00"); // Response code (00 for success)
        response.set(54, "000000150000"); // Mock balance (e.g., 1500.00)
        response.set(102, request.getString(102)); // Bank Account Number
        response.set(103, request.getString(103)); // Customer Full Name

        // Log the response message with masking
        logger.info("ISO 8583 Response Message: {}", DataMaskingUtil.maskIso8583Log(isoMsgToString(response)));

        return response;
    }

    /**
     * Converts an ISO message to a string format for logging.
     *
     * @param isoMsg The ISO message.
     * @return The string representation of the message.
     * @throws ISOException If there is an error parsing the message.
     */
    private String isoMsgToString(ISOMsg isoMsg) throws ISOException {
        StringBuilder sb = new StringBuilder();
        sb.append("MTI: ").append(isoMsg.getMTI()).append(", ");
        for (int i = 1; i <= isoMsg.getMaxField(); i++) {
            if (isoMsg.hasField(i)) {
                sb.append("Field ").append(i).append(": ").append(isoMsg.getString(i)).append(", ");
            }
        }
        return sb.toString();
    }
    
}
