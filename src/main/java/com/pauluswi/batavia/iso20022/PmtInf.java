package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class PmtInf {

    @JacksonXmlProperty(localName = "Dbtr")
    private Dbtr dbtr;

    @JacksonXmlProperty(localName = "DbtrAcct")
    private DbtrAcct dbtrAcct;
}
