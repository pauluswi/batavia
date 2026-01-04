package com.pauluswi.batavia.service;

import com.pauluswi.batavia.annotation.Idempotent;
import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.dto.FundTransferResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.demo.ISO8583Service;
import com.pauluswi.batavia.util.DataMaskingUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundTransferService {

    private static final Logger logger = LoggerFactory.getLogger(FundTransferService.class);

    @Autowired
    private ISO8583Service iso8583Service;

    @Idempotent(headerName = "X-Request-ID")
    @CircuitBreaker(name = "backendA", fallbackMethod = "fallbackFundTransfer")
    public FundTransferResponseDTO transferFunds(FundTransferRequestDTO requestDTO) {
        logger.info("Processing fund transfer from {} to {}", 
                DataMaskingUtil.mask(requestDTO.getSourceAccountNumber()), 
                DataMaskingUtil.mask(requestDTO.getDestinationAccountNumber()));

        try {
            // 1. Create ISO 8583 Request
            ISOMsg isoRequest = iso8583Service.createFundTransferRequest(
                    requestDTO.getSourceAccountNumber(), 
                    requestDTO.getDestinationAccountNumber(), 
                    requestDTO.getAmount());

            // 2. Simulate Core Response
            ISOMsg isoResponse = iso8583Service.createFundTransferResponse(isoRequest);

            // 3. Parse Response
            return parseIsoResponse(isoResponse);

        } catch (ISOException e) {
            logger.error("Error processing fund transfer", e);
            throw new RuntimeException("Fund transfer failed", e);
        }
    }

    public FundTransferResponseDTO fallbackFundTransfer(FundTransferRequestDTO requestDTO, Throwable t) {
        logger.error("Fallback triggered for fund transfer: {}", t.getMessage());
        FundTransferResponseDTO response = new FundTransferResponseDTO();
        response.setResponseCode(ErrorCode.SYSTEM_ERROR.getCode());
        response.setMessage("Transaction failed due to system error");
        return response;
    }

    private FundTransferResponseDTO parseIsoResponse(ISOMsg isoMsg) throws ISOException {
        FundTransferResponseDTO response = new FundTransferResponseDTO();
        String responseCode = isoMsg.getString(39);
        response.setResponseCode(responseCode);
        
        if (ErrorCode.SUCCESS.getCode().equals(responseCode)) {
            response.setTransactionId(isoMsg.getString(37)); // RRN
            response.setMessage("Transfer Successful");
        } else {
            response.setMessage("Transfer Failed");
        }
        return response;
    }
}
