package com.pauluswi.batavia.service;

import com.pauluswi.batavia.util.DataMaskingUtil;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.CustomerBalanceRequestDTO;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.demo.ISO8583Service;

@Service
public class Customer8583Service {

    private static final Logger logger = LoggerFactory.getLogger(Customer8583Service.class);
    
    @Autowired
    private ISO8583Service iso8583Service;

    /**
     * Gets the customer balance by simulating an ISO 8583 integration.
     *
     * @param requestDTO The customer balance inquiry request.
     * @return The customer balance response in JSON format.
     */
    @Retryable(value = {ISOException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CustomerBalanceResponseDTO getCustomerBalance(CustomerBalanceRequestDTO requestDTO) {
        logger.info("Processing balance inquiry for account: {}", DataMaskingUtil.mask(requestDTO.getBankAccountNumber()));
        try {
            // Step 1: Create ISO 8583 request
            ISOMsg isoRequest = iso8583Service.createBalanceInquiryRequest(requestDTO.getBankAccountNumber(), requestDTO.getCustomerFullName());

            // Step 2: Simulate core system response
            ISOMsg isoResponse = iso8583Service.createBalanceInquiryResponse(isoRequest);

            // Step 3: Convert ISO 8583 response to JSON
            return parseIsoResponseToDto(isoResponse);

        } catch (ISOException e) {
            logger.error("Error processing ISO 8583 message", e);
            // In a real retry scenario, we might rethrow to trigger retry or return a specific error DTO
            // For now, returning null or error DTO as per original logic, but logging error.
            // To trigger retry, we should rethrow.
            throw new RuntimeException("ISO 8583 processing failed", e);
        }
    }

    /**
     * Parses the ISO 8583 response message into JSON format using DTO.
     *
     * @param isoMsg The ISO message.
     * @return The JSON representation of the response.
     * @throws ISOException If there is an error parsing the message.
     */
    private CustomerBalanceResponseDTO parseIsoResponseToDto(ISOMsg isoMsg) throws ISOException {
        CustomerBalanceResponseDTO responseDTO = new CustomerBalanceResponseDTO();
        responseDTO.setMTI(isoMsg.getMTI());
        
        String responseCode = isoMsg.getString(39);
        responseDTO.setResponseCode(responseCode);

        if (ErrorCode.SUCCESS.getCode().equals(responseCode)) {
             BalanceDataDTO dataDTO = new BalanceDataDTO();
             dataDTO.setBankAccountNumber(isoMsg.getString(102));
             dataDTO.setCustomerFullName(isoMsg.getString(103));
             dataDTO.setBalance(Double.parseDouble(isoMsg.getString(54)));
             responseDTO.setData(dataDTO);
        }

        return responseDTO;
    }
}
