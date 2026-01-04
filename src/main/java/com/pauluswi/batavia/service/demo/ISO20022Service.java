package com.pauluswi.batavia.service.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pauluswi.batavia.iso20022.*;
import com.pauluswi.batavia.util.DataMaskingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
}
