package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class PmtInf {

    @JacksonXmlProperty(localName = "PmtInfId")
    private String pmtInfId;

    @JacksonXmlProperty(localName = "PmtMtd")
    private String pmtMtd;

    @JacksonXmlProperty(localName = "Dbtr")
    private Dbtr dbtr;

    @JacksonXmlProperty(localName = "DbtrAcct")
    private DbtrAcct dbtrAcct;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "CdtTrfTxInf")
    private List<CdtTrfTxInf> cdtTrfTxInf;
}
