package com.pauluswi.batavia.service;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pauluswi.batavia.service.demo.ISO8583Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerBalanceService {
    
    @Autowired
    private ISO8583Service iso8583Service;

    /**
     * Gets the customer balance by simulating an ISO 8583 integration.
     *
     * @param customerId The customer ID.
     * @return The customer balance response in JSON format.
     */
    public Map<String, Object> getCustomerBalance(String customerId) {
        try {
            // Step 1: Create ISO 8583 request
            ISOMsg isoRequest = iso8583Service.createBalanceInquiryRequest(customerId);

            // Step 2: Simulate core system response
            ISOMsg isoResponse = iso8583Service.createBalanceInquiryResponse(isoRequest);

            // Step 3: Convert ISO 8583 response to JSON
            return parseIsoResponseToJson(isoResponse);

        } catch (ISOException e) {
            e.printStackTrace();
            return Map.of("error", "Failed to process ISO 8583 message");
        }
    }

    /**
     * Parses the ISO 8583 response message into JSON format.
     *
     * @param isoMsg The ISO message.
     * @return The JSON representation of the response.
     * @throws ISOException If there is an error parsing the message.
     */
    private Map<String, Object> parseIsoResponseToJson(ISOMsg isoMsg) throws ISOException {
        Map<String, Object> response = new HashMap<>();
        response.put("MTI", isoMsg.getMTI());
        response.put("responseCode", isoMsg.getString(39));
        response.put("balance", parseBalance(isoMsg.getString(54)));
        return response;
    }

    private double parseBalance(String balanceField) {
        return Double.parseDouble(balanceField);
    }
}

