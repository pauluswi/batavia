package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class PmtId {

    @JacksonXmlProperty(localName = "InstrId")
    private String instrId;

    @JacksonXmlProperty(localName = "EndToEndId")
    private String endToEndId;
}
