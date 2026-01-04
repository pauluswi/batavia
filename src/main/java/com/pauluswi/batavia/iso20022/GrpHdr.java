package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class GrpHdr {

    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;

    @JacksonXmlProperty(localName = "CreDtTm")
    private String creDtTm;
}
