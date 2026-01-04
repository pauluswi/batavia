package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class CdtTrfTxInf {

    @JacksonXmlProperty(localName = "PmtId")
    private PmtId pmtId;

    @JacksonXmlProperty(localName = "InstdAmt")
    private InstdAmt instdAmt;

    @JacksonXmlProperty(localName = "Dbtr")
    private Dbtr dbtr;

    @JacksonXmlProperty(localName = "DbtrAcct")
    private DbtrAcct dbtrAcct;

    @JacksonXmlProperty(localName = "Cdtr")
    private Cdtr cdtr;

    @JacksonXmlProperty(localName = "CdtrAcct")
    private CdtrAcct cdtrAcct;
}
