package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class CstmrCdtTrfInitn {

    @JacksonXmlProperty(localName = "GrpHdr")
    private GrpHdr grpHdr;

    @JacksonXmlProperty(localName = "PmtInf")
    private PmtInf pmtInf;

    @JacksonXmlProperty(localName = "Bal")
    private Bal bal;

    @JacksonXmlProperty(localName = "AcctInf")
    private AcctInf acctInf;
}
