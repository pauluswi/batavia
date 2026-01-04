package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class CdtrAcct {

    @JacksonXmlProperty(localName = "Id")
    private Id id;
}
