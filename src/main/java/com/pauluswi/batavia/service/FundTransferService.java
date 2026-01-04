package com.pauluswi.batavia.service;

import com.pauluswi.batavia.annotation.Idempotent;
import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.dto.FundTransferResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.service.demo.ISO20022Service;
import com.pauluswi.batavia.service.demo.ISO8583Service;
import com.pauluswi.batavia.util.DataMaskingUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import java.io.StringReader;

@Service
public class FundTransferService {

    private static final Logger logger = LoggerFactory.getLogger(FundTransferService.class);

    @Autowired
    private ISO8583Service iso8583Service;

    @Autowired
    private ISO20022Service iso20022Service;

    @Idempotent(headerName = "X-Request-ID")
    @CircuitBreaker(name = "backendA", fallbackMethod = "fallbackFundTransfer")
    public FundTransferResponseDTO transferFunds(String protocol, FundTransferRequestDTO requestDTO) {
        logger.info("Processing fund transfer from {} to {} via protocol {}",
                DataMaskingUtil.mask(requestDTO.getSourceAccountNumber()),
                DataMaskingUtil.mask(requestDTO.getDestinationAccountNumber()),
                protocol);

        if ("8583".equals(protocol)) {
            return handle8583Transfer(requestDTO);
        } else if ("20022".equals(protocol)) {
            return handle20022Transfer(requestDTO);
        } else {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
    }

    private FundTransferResponseDTO handle8583Transfer(FundTransferRequestDTO requestDTO) {
        try {
            ISOMsg isoRequest = iso8583Service.createFundTransferRequest(
                    requestDTO.getSourceAccountNumber(),
                    requestDTO.getDestinationAccountNumber(),
                    requestDTO.getAmount());

            ISOMsg isoResponse = iso8583Service.createFundTransferResponse(isoRequest);

            return parseIso8583Response(isoResponse);

        } catch (ISOException e) {
            logger.error("Error processing ISO 8583 fund transfer", e);
            throw new RuntimeException("ISO 8583 fund transfer failed", e);
        }
    }

    private FundTransferResponseDTO handle20022Transfer(FundTransferRequestDTO requestDTO) {
        String isoRequest = iso20022Service.buildFundTransferRequest(requestDTO);
        String isoResponse = iso20022Service.simulateFundTransferResponse(isoRequest);
        return parseIso20022Response(isoResponse);
    }

    public FundTransferResponseDTO fallbackFundTransfer(String protocol, FundTransferRequestDTO requestDTO, Throwable t) {
        logger.error("Fallback triggered for fund transfer: {}", t.getMessage());
        FundTransferResponseDTO response = new FundTransferResponseDTO();
        response.setResponseCode(ErrorCode.SYSTEM_ERROR.getCode());
        response.setMessage("Transaction failed due to system error");
        return response;
    }

    private FundTransferResponseDTO parseIso8583Response(ISOMsg isoMsg) throws ISOException {
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

    private FundTransferResponseDTO parseIso20022Response(String xmlResponse) {
        FundTransferResponseDTO response = new FundTransferResponseDTO();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            InputSource source = new InputSource(new StringReader(xmlResponse));

            String txSts = (String) xpath.evaluate("//*[local-name()='TxSts']/text()", source, XPathConstants.STRING);

            if ("ACSC".equals(txSts)) {
                response.setResponseCode(ErrorCode.SUCCESS.getCode());
                response.setMessage("Transfer Successful");
                source = new InputSource(new StringReader(xmlResponse)); // Re-create source
                String txId = (String) xpath.evaluate("//*[local-name()='TxId']/text()", source, XPathConstants.STRING);
                response.setTransactionId(txId);
            } else {
                response.setResponseCode(ErrorCode.SYSTEM_ERROR.getCode());
                response.setMessage("Transfer Failed");
            }
        } catch (Exception e) {
            logger.error("Error parsing ISO 20022 response", e);
            response.setResponseCode(ErrorCode.SYSTEM_ERROR.getCode());
            response.setMessage("Error parsing response");
        }
        return response;
    }
}
