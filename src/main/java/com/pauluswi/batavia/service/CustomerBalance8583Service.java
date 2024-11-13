package com.pauluswi.batavia.service;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.BalanceInquiryRequestDTO;
import com.pauluswi.batavia.dto.BalanceInquiryResponseDTO;
import com.pauluswi.batavia.service.demo.ISO8583Service;

@Service
public class CustomerBalance8583Service {
    
    @Autowired
    private ISO8583Service iso8583Service;

    /**
     * Gets the customer balance by simulating an ISO 8583 integration.
     *
     * @param customerId The customer ID.
     * @return The customer balance response in JSON format.
     */
    public BalanceInquiryResponseDTO getCustomerBalance(BalanceInquiryRequestDTO requestDTO) {
        try {
            // Step 1: Create ISO 8583 request
            ISOMsg isoRequest = iso8583Service.createBalanceInquiryRequest(requestDTO.getBankAccountNumber(), requestDTO.getCustomerFullName());

            // Step 2: Simulate core system response
            ISOMsg isoResponse = iso8583Service.createBalanceInquiryResponse(isoRequest);

            // Step 3: Convert ISO 8583 response to JSON
            return parseIsoResponseToDto(isoResponse);

        } catch (ISOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses the ISO 8583 response message into JSON format using DTO.
     *
     * @param isoMsg The ISO message.
     * @return The JSON representation of the response.
     * @throws ISOException If there is an error parsing the message.
     */
    private BalanceInquiryResponseDTO parseIsoResponseToDto(ISOMsg isoMsg) throws ISOException {
        BalanceInquiryResponseDTO responseDTO = new BalanceInquiryResponseDTO();
        responseDTO.setMTI(isoMsg.getMTI());
        responseDTO.setResponseCode(isoMsg.getString(39));

        BalanceDataDTO dataDTO = new BalanceDataDTO();
        dataDTO.setBankAccountNumber(isoMsg.getString(102));
        dataDTO.setCustomerFullName(isoMsg.getString(103));
        dataDTO.setBalance(Double.parseDouble(isoMsg.getString(54)));

        responseDTO.setData(dataDTO);
        return responseDTO;
    }
}

