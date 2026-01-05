package com.pauluswi.batavia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pauluswi.batavia.dto.BalanceDataDTO;
import com.pauluswi.batavia.dto.CustomerBalanceRequestDTO;
import com.pauluswi.batavia.dto.CustomerBalanceResponseDTO;
import com.pauluswi.batavia.exception.ErrorCode;
import com.pauluswi.batavia.iso20022.Document;
import com.pauluswi.batavia.service.demo.ISO20022Service;
import com.pauluswi.batavia.util.DataMaskingUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class Customer20022Service {

    private static final Logger logger = LoggerFactory.getLogger(Customer20022Service.class);

    @Autowired
    private ISO20022Service iso20022Service;

    private final XmlMapper xmlMapper = new XmlMapper();

    @CircuitBreaker(name = "backendA", fallbackMethod = "fallbackGetCustomerBalance")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CustomerBalanceResponseDTO getCustomerBalance(CustomerBalanceRequestDTO requestDTO) {
        logger.info("Processing ISO 20022 balance inquiry for account: {}", DataMaskingUtil.mask(requestDTO.getBankAccountNumber()));

        String iso20022Request = iso20022Service.buildBalanceInquiryRequest(
                requestDTO.getBankAccountNumber(), requestDTO.getCustomerFullName());

        String iso20022Response = iso20022Service.simulateBalanceInquiryResponse(iso20022Request);

        try {
            return parseXmlResponseToDto(iso20022Response);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing ISO 20022 balance response", e);
            throw new RuntimeException("Failed to parse balance response", e);
        }
    }

    public CustomerBalanceResponseDTO fallbackGetCustomerBalance(CustomerBalanceRequestDTO requestDTO, Throwable t) {
        logger.error("Fallback triggered for account: {} due to: {}", DataMaskingUtil.mask(requestDTO.getBankAccountNumber()), t.getMessage());
        CustomerBalanceResponseDTO responseDTO = new CustomerBalanceResponseDTO();
        responseDTO.setResponseCode(ErrorCode.SYSTEM_ERROR.getCode());
        return responseDTO;
    }

    private CustomerBalanceResponseDTO parseXmlResponseToDto(String iso20022Response) throws JsonProcessingException {
        Document doc = xmlMapper.readValue(iso20022Response, Document.class);

        CustomerBalanceResponseDTO responseDTO = new CustomerBalanceResponseDTO();
        responseDTO.setResponseCode(ErrorCode.SUCCESS.getCode());
        responseDTO.setMTI(doc.getCstmrCdtTrfInitn().getGrpHdr().getMsgId());

        BalanceDataDTO dataDTO = new BalanceDataDTO();
        dataDTO.setBankAccountNumber(doc.getCstmrCdtTrfInitn().getPmtInf().getDbtrAcct().getId().getOthr().getId());
        dataDTO.setCustomerFullName(doc.getCstmrCdtTrfInitn().getAcctInf().getName());
        dataDTO.setBalance(Double.parseDouble(doc.getCstmrCdtTrfInitn().getBal().getAmt().getValue()));

        responseDTO.setData(dataDTO);
        return responseDTO;
    }
}
