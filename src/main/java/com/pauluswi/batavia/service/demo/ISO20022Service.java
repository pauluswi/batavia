package com.pauluswi.batavia.service.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pauluswi.batavia.dto.FundTransferRequestDTO;
import com.pauluswi.batavia.iso20022.*;
import com.pauluswi.batavia.util.DataMaskingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class ISO20022Service {

    private static final Logger logger = LoggerFactory.getLogger(ISO20022Service.class);
    private final XmlMapper xmlMapper = new XmlMapper();

    public String buildBalanceInquiryRequest(String bankAccountNumber, String customerFullName) {
        try {
            Document document = new Document();
            CstmrCdtTrfInitn initn = new CstmrCdtTrfInitn();

            GrpHdr grpHdr = new GrpHdr();
            grpHdr.setMsgId("msg123456");
            grpHdr.setCreDtTm("2024-01-01T12:00:00");
            initn.setGrpHdr(grpHdr);

            PmtInf pmtInf = new PmtInf();
            Dbtr dbtr = new Dbtr();
            dbtr.setNm(customerFullName);
            pmtInf.setDbtr(dbtr);

            DbtrAcct dbtrAcct = new DbtrAcct();
            Id id = new Id();
            Othr othr = new Othr();
            othr.setId(bankAccountNumber);
id.setOthr(othr);
            dbtrAcct.setId(id);
            pmtInf.setDbtrAcct(dbtrAcct);

            initn.setPmtInf(pmtInf);
            document.setCstmrCdtTrfInitn(initn);

            String requestData = xmlMapper.writeValueAsString(document);

            // Log the request message with masking
            logger.info("ISO 20022 Request Message: {}", DataMaskingUtil.maskIso20022(requestData));

            return requestData;
        } catch (JsonProcessingException e) {
            logger.error("Error building ISO 20022 request", e);
            throw new RuntimeException("Error building ISO 20022 request", e);
        }
    }

    public String simulateBalanceInquiryResponse(String requestXml) {
        try {
            Document document = new Document();
            CstmrCdtTrfInitn initn = new CstmrCdtTrfInitn();

            GrpHdr grpHdr = new GrpHdr();
            grpHdr.setMsgId("msg123456");
            grpHdr.setCreDtTm("2024-01-01T12:00:00");
            initn.setGrpHdr(grpHdr);

            PmtInf pmtInf = new PmtInf();
            DbtrAcct dbtrAcct = new DbtrAcct();
            Id id = new Id();
            Othr othr = new Othr();
            othr.setId("123456");
id.setOthr(othr);
            dbtrAcct.setId(id);
            pmtInf.setDbtrAcct(dbtrAcct);
            initn.setPmtInf(pmtInf);

            Bal bal = new Bal();
            Amt amt = new Amt();
            amt.setCcy("USD");
            amt.setValue("1500.00");
            bal.setAmt(amt);
            initn.setBal(bal);

            AcctInf acctInf = new AcctInf();
            acctInf.setCif("111");
            acctInf.setName("Andi Lukito");
            initn.setAcctInf(acctInf);

            document.setCstmrCdtTrfInitn(initn);

            String responseData = xmlMapper.writeValueAsString(document);

            // Log the response message with masking
            logger.info("ISO 20022 Response Message: {}", DataMaskingUtil.maskIso20022(responseData));

            return responseData;
        } catch (JsonProcessingException e) {
            logger.error("Error simulating ISO 20022 response", e);
            throw new RuntimeException("Error simulating ISO 20022 response", e);
        }
    }

    public String buildFundTransferRequest(FundTransferRequestDTO requestDTO) {
        try {
            Document document = new Document();
            CstmrCdtTrfInitn initn = new CstmrCdtTrfInitn();

            GrpHdr grpHdr = new GrpHdr();
            grpHdr.setMsgId("msg-" + UUID.randomUUID().toString());
            grpHdr.setCreDtTm("2024-01-01T12:00:00");
            initn.setGrpHdr(grpHdr);

            PmtInf pmtInf = new PmtInf();
            pmtInf.setPmtInfId("pmt-" + UUID.randomUUID().toString());
            pmtInf.setPmtMtd("TRF");

            Dbtr dbtr = new Dbtr();
            // In a real scenario, you'd look up the debtor name
            dbtr.setNm("Source Customer");
            pmtInf.setDbtr(dbtr);

            DbtrAcct dbtrAcct = new DbtrAcct();
            Id dbtrId = new Id();
            Othr dbtrOthr = new Othr();
            dbtrOthr.setId(requestDTO.getSourceAccountNumber());
            dbtrId.setOthr(dbtrOthr);
            dbtrAcct.setId(dbtrId);
            pmtInf.setDbtrAcct(dbtrAcct);

            CdtTrfTxInf cdtTrfTxInf = new CdtTrfTxInf();
            PmtId pmtId = new PmtId();
            pmtId.setInstrId("instr-" + UUID.randomUUID().toString());
            pmtId.setEndToEndId("e2e-" + UUID.randomUUID().toString());
            cdtTrfTxInf.setPmtId(pmtId);

            InstdAmt instdAmt = new InstdAmt();
            instdAmt.setCcy(requestDTO.getCurrency());
            instdAmt.setValue(String.valueOf(requestDTO.getAmount()));
            cdtTrfTxInf.setInstdAmt(instdAmt);

            Cdtr cdtr = new Cdtr();
            // In a real scenario, you'd look up the creditor name
            cdtr.setNm("Destination Customer");
            cdtTrfTxInf.setCdtr(cdtr);

            CdtrAcct cdtrAcct = new CdtrAcct();
            Id cdtrId = new Id();
            Othr cdtrOthr = new Othr();
            cdtrOthr.setId(requestDTO.getDestinationAccountNumber());
            cdtrId.setOthr(cdtrOthr);
            cdtrAcct.setId(cdtrId);
            cdtTrfTxInf.setCdtrAcct(cdtrAcct);

            pmtInf.setCdtTrfTxInf(Collections.singletonList(cdtTrfTxInf));
            initn.setPmtInf(pmtInf);
            document.setCstmrCdtTrfInitn(initn);

            String requestData = xmlMapper.writeValueAsString(document);

            logger.info("ISO 20022 Fund Transfer Request: {}", DataMaskingUtil.maskIso20022(requestData));
            return requestData;
        } catch (JsonProcessingException e) {
            logger.error("Error building ISO 20022 fund transfer request", e);
            throw new RuntimeException("Error building ISO 20022 fund transfer request", e);
        }
    }

    public String simulateFundTransferResponse(String requestXml) {
        // For a real implementation, you would parse the request to get IDs
        // For this simulation, we'll just create a static response
        String transactionId = "trx-" + UUID.randomUUID().toString();
        String responseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.002.001.03\">" +
                "<FIToFIPmtStsRpt>" +
                "<GrpHdr><MsgId>msg-resp-" + UUID.randomUUID().toString() + "</MsgId></GrpHdr>" +
                "<TxInfAndSts>" +
                "<OrgnlEndToEndId>e2e-from-request</OrgnlEndToEndId>" +
                "<TxSts>ACSC</TxSts>" + // AcceptedSettlementCompleted
                "<TxId>" + transactionId + "</TxId>" +
                "</TxInfAndSts>" +
                "</FIToFIPmtStsRpt>" +
                "</Document>";

        logger.info("ISO 20022 Fund Transfer Response: {}", responseXml);
        return responseXml;
    }
}
