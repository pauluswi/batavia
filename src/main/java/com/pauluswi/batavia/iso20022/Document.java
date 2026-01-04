package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03")
public class Document {

    @JacksonXmlProperty(localName = "CstmrCdtTrfInitn")
    private CstmrCdtTrfInitn cstmrCdtTrfInitn;
}
